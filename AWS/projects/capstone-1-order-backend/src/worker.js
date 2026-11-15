"use strict";

const http = require("node:http");
const crypto = require("node:crypto");
const domain = require("./domain");
const { createLogger } = require("./logger");
const { createPgPool } = require("./db");

const POLL_INTERVAL_MS = Number(process.env.POLL_INTERVAL_MS || 1000);
const BATCH_SIZE = Number(process.env.WORKER_BATCH_SIZE || 25);
const BASE_BACKOFF_MS = Number(process.env.BASE_BACKOFF_MS || 200);
const MAX_BACKOFF_MS = Number(process.env.MAX_BACKOFF_MS || 15000);
const POISON_SKU = process.env.POISON_SKU || "SKU-POISON-99";
const WORKER_HEALTH_PORT = Number(process.env.WORKER_HEALTH_PORT || 3001);

const log = createLogger("order-worker");
const pool = createPgPool();

let isShuttingDown = false;
let activePollPromise = null;
let pollTimer = null;
let lastPollOkAt = Date.now();

async function deliverDownstream(evt) {
  const payload = evt.payload || {};
  if (payload.simulateFailure === true || payload.sku === POISON_SKU) {
    throw new Error("Simulated downstream fulfillment failure for poison/test payload");
  }
  // In production this is where an idempotent downstream HTTP/payment/ledger call runs.
  return { delivered: true };
}

async function processNextOutboxEvent() {
  const client = await pool.connect();
  let inTx = false;

  try {
    await client.query("BEGIN");
    inTx = true;

    const claimRes = await client.query(
      `SELECT event_id, order_id, event_type, payload, attempts, max_attempts
         FROM outbox_events
        WHERE status = 'PENDING'
          AND next_run_at <= now()
        ORDER BY next_run_at ASC, created_at ASC
        FOR UPDATE SKIP LOCKED
        LIMIT 1`
    );

    if (claimRes.rowCount === 0) {
      await client.query("COMMIT");
      inTx = false;
      return false;
    }

    const evt = claimRes.rows[0];
    const payload = evt.payload || {};
    const traceId = payload.traceId || "00000000000000000000000000000000";

    // Idempotent consumer check: skip side effects if event_id was already committed
    const dupCheck = await client.query(
      "SELECT 1 FROM processed_events WHERE event_id = $1",
      [evt.event_id]
    );

    if (dupCheck.rowCount > 0) {
      await client.query(
        `UPDATE outbox_events
            SET status = 'PROCESSED',
                processed_at = now(),
                last_error = NULL
          WHERE event_id = $1`,
        [evt.event_id]
      );
      await client.query("COMMIT");
      inTx = false;
      log.info("outbox_duplicate_ack", {
        traceId,
        eventId: evt.event_id,
        orderId: evt.order_id
      });
      return true;
    }

    try {
      await deliverDownstream(evt);

      const dedupInsert = await client.query(
        `INSERT INTO processed_events (event_id)
         VALUES ($1)
         ON CONFLICT (event_id) DO NOTHING
         RETURNING event_id`,
        [evt.event_id]
      );

      if (dedupInsert.rowCount === 1) {
        const entryId = crypto.randomUUID();
        await client.query(
          `INSERT INTO ledger_entries (entry_id, order_id, event_id, sku, qty)
           VALUES ($1, $2, $3, $4, $5)
           ON CONFLICT (order_id) DO NOTHING`,
          [entryId, evt.order_id, evt.event_id, payload.sku, Number(payload.qty)]
        );
        await client.query(
          `UPDATE orders
              SET status = 'CONFIRMED',
                  updated_at = now()
            WHERE order_id = $1`,
          [evt.order_id]
        );
      }

      await client.query(
        `UPDATE outbox_events
            SET status = 'PROCESSED',
                processed_at = now(),
                last_error = NULL
          WHERE event_id = $1`,
        [evt.event_id]
      );

      await client.query("COMMIT");
      inTx = false;

      log.info("outbox_event_processed", {
        traceId,
        eventId: evt.event_id,
        orderId: evt.order_id,
        sku: payload.sku,
        qty: payload.qty,
        attempts: Number(evt.attempts) + 1
      });
    } catch (deliveryErr) {
      const nextAttempts = Number(evt.attempts) + 1;
      const maxAttempts = Number(evt.max_attempts || 5);
      const failureAction = domain.decideWorkerFailureAction(
        nextAttempts,
        maxAttempts,
        BASE_BACKOFF_MS,
        MAX_BACKOFF_MS
      );

      if (failureAction.nextStatus === "DLQ") {
        await client.query(
          `UPDATE outbox_events
              SET attempts = $2,
                  status = 'DLQ',
                  last_error = $3
            WHERE event_id = $1`,
          [evt.event_id, nextAttempts, deliveryErr.message]
        );
        await client.query(
          `UPDATE orders
              SET status = 'FAILED_DLQ',
                  updated_at = now()
            WHERE order_id = $1`,
          [evt.order_id]
        );
        await client.query("COMMIT");
        inTx = false;

        log.error("outbox_event_moved_to_dlq", {
          traceId,
          eventId: evt.event_id,
          orderId: evt.order_id,
          attempts: nextAttempts,
          maxAttempts,
          error: deliveryErr.message
        });
      } else {
        await client.query(
          `UPDATE outbox_events
              SET attempts = $2,
                  next_run_at = now() + ($3 * interval '1 millisecond'),
                  last_error = $4
            WHERE event_id = $1`,
          [evt.event_id, nextAttempts, failureAction.delayMs, deliveryErr.message]
        );
        await client.query("COMMIT");
        inTx = false;

        log.warn("outbox_event_retry_scheduled", {
          traceId,
          eventId: evt.event_id,
          orderId: evt.order_id,
          attempts: nextAttempts,
          maxAttempts,
          backoffMs: failureAction.delayMs,
          error: deliveryErr.message
        });
      }
    }

    return true;
  } catch (err) {
    if (inTx) {
      await client.query("ROLLBACK").catch(() => {});
    }
    log.error("worker_poll_error", { error: err.message });
    return false;
  } finally {
    client.release();
  }
}

async function runPollCycle() {
  if (isShuttingDown || activePollPromise) {
    return;
  }

  activePollPromise = (async () => {
    try {
      let processed = 0;
      while (!isShuttingDown && processed < BATCH_SIZE) {
        const hadWork = await processNextOutboxEvent();
        if (!hadWork) {
          break;
        }
        processed += 1;
      }
      lastPollOkAt = Date.now();
    } finally {
      activePollPromise = null;
    }
  })();

  await activePollPromise;
}

const healthServer = http.createServer(async (req, res) => {
  if (req.method === "GET" && req.url === "/healthz") {
    if (isShuttingDown) {
      res.writeHead(503, { "content-type": "application/json" });
      res.end(JSON.stringify({ status: "shutting_down" }));
      return;
    }
    try {
      await pool.query("SELECT 1");
      res.writeHead(200, { "content-type": "application/json" });
      res.end(JSON.stringify({ status: "ok", lastPollOkAt: new Date(lastPollOkAt).toISOString() }));
    } catch (err) {
      res.writeHead(503, { "content-type": "application/json" });
      res.end(JSON.stringify({ status: "db_error", error: err.message }));
    }
    return;
  }
  res.writeHead(404, { "content-type": "application/json" });
  res.end(JSON.stringify({ error: "NOT_FOUND" }));
});

async function shutdown(signal) {
  if (isShuttingDown) {
    return;
  }
  isShuttingDown = true;
  log.info("worker_shutdown_started", { signal });

  if (pollTimer) {
    clearInterval(pollTimer);
    pollTimer = null;
  }

  if (activePollPromise) {
    await activePollPromise;
  }

  await new Promise((resolve) => healthServer.close(() => resolve()));
  await pool.end();
  log.info("worker_shutdown_complete", { signal });
  process.exit(0);
}

process.on("SIGTERM", () => shutdown("SIGTERM"));
process.on("SIGINT", () => shutdown("SIGINT"));

healthServer.listen(WORKER_HEALTH_PORT, "0.0.0.0", () => {
  log.info("worker_started", {
    pollIntervalMs: POLL_INTERVAL_MS,
    batchSize: BATCH_SIZE,
    baseBackoffMs: BASE_BACKOFF_MS,
    maxBackoffMs: MAX_BACKOFF_MS,
    healthPort: WORKER_HEALTH_PORT
  });

  runPollCycle();
  pollTimer = setInterval(runPollCycle, POLL_INTERVAL_MS);
});
