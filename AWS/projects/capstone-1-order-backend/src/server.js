"use strict";

const http = require("node:http");
const crypto = require("node:crypto");
const Redis = require("ioredis");
const domain = require("./domain");
const { createLogger } = require("./logger");
const { createPgPool } = require("./db");

const PORT = Number(process.env.PORT || 3000);
const SHUTDOWN_GRACE_MS = Number(process.env.SHUTDOWN_GRACE_MS || 25000);
const MAX_BODY_BYTES = 64 * 1024;
const MAX_OPTIMISTIC_RETRIES = 3;
const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

const log = createLogger("order-api");
const pool = createPgPool();

function createRedisClient() {
  if (process.env.REDIS_ENABLED === "false") {
    return null;
  }
  const url = process.env.REDIS_URL || "redis://localhost:6379";
  const client = new Redis(url, {
    lazyConnect: true,
    maxRetriesPerRequest: 1,
    connectTimeout: 1500,
    enableOfflineQueue: false
  });
  client.on("error", (err) => {
    log.warn("redis_client_error", { error: err.message });
  });
  return client;
}

const redis = createRedisClient();

function sendJson(res, statusCode, body, extraHeaders) {
  const payload = JSON.stringify(body);
  const headers = Object.assign(
    {
      "content-type": "application/json; charset=utf-8",
      "content-length": Buffer.byteLength(payload)
    },
    extraHeaders || {}
  );
  res.writeHead(statusCode, headers);
  res.end(payload);
}

function readJsonBody(req) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    let totalBytes = 0;

    req.on("data", (chunk) => {
      totalBytes += chunk.length;
      if (totalBytes > MAX_BODY_BYTES) {
        const err = new Error("Request body too large");
        err.statusCode = 413;
        err.code = "PAYLOAD_TOO_LARGE";
        req.destroy(err);
        reject(err);
        return;
      }
      chunks.push(chunk);
    });

    req.on("end", () => {
      if (chunks.length === 0) {
        resolve({});
        return;
      }
      try {
        const raw = Buffer.concat(chunks).toString("utf8");
        resolve(JSON.parse(raw));
      } catch (_parseErr) {
        const err = new Error("Malformed JSON body");
        err.statusCode = 400;
        err.code = "INVALID_JSON";
        reject(err);
      }
    });

    req.on("error", (err) => {
      reject(err);
    });
  });
}

async function handleReadyz(res, traceCtx, isShuttingDown) {
  if (isShuttingDown) {
    return sendJson(
      res,
      503,
      { status: "shutting_down", traceId: traceCtx.traceId },
      { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
    );
  }

  let pgStatus = "down";
  try {
    await pool.query("SELECT 1");
    pgStatus = "up";
  } catch (err) {
    log.error("readyz_postgres_failed", { traceId: traceCtx.traceId, error: err.message });
  }

  let redisStatus = "disabled";
  if (redis) {
    try {
      if (redis.status === "wait") {
        await redis.connect();
      }
      await redis.ping();
      redisStatus = "up";
    } catch (_err) {
      redisStatus = "degraded";
    }
  }

  const healthy = pgStatus === "up";
  return sendJson(
    res,
    healthy ? 200 : 503,
    {
      status: healthy ? "ready" : "not_ready",
      dependencies: { postgres: pgStatus, redis: redisStatus },
      traceId: traceCtx.traceId
    },
    { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
  );
}

async function handleGetInventory(sku, res, traceCtx) {
  const cacheKey = "inventory:" + sku;
  const headers = { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId };

  if (redis && redis.status === "ready") {
    try {
      const cached = await redis.get(cacheKey);
      if (cached) {
        return sendJson(
          res,
          200,
          Object.assign(JSON.parse(cached), { cache: "HIT", traceId: traceCtx.traceId }),
          headers
        );
      }
    } catch (err) {
      log.warn("redis_get_failed", { traceId: traceCtx.traceId, sku, error: err.message });
    }
  }

  const result = await pool.query(
    "SELECT sku, name, available, version, updated_at FROM inventory WHERE sku = $1",
    [sku]
  );

  if (result.rowCount === 0) {
    return sendJson(res, 404, { error: "SKU_NOT_FOUND", sku, traceId: traceCtx.traceId }, headers);
  }

  const row = result.rows[0];
  if (redis && redis.status === "ready") {
    const ttlWithJitterSec = 30 + Math.floor(Math.random() * 6);
    redis
      .set(cacheKey, JSON.stringify(row), "EX", ttlWithJitterSec)
      .catch((err) => log.warn("redis_set_failed", { traceId: traceCtx.traceId, error: err.message }));
  }

  return sendJson(res, 200, Object.assign({}, row, { cache: "MISS", traceId: traceCtx.traceId }), headers);
}

async function handleGetOrder(orderId, res, traceCtx) {
  const headers = { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId };
  if (!UUID_RE.test(orderId)) {
    return sendJson(res, 400, { error: "INVALID_ORDER_ID", traceId: traceCtx.traceId }, headers);
  }

  const result = await pool.query(
    `SELECT o.order_id, o.sku, o.qty, o.status, o.trace_id, o.created_at, o.updated_at,
            e.event_id, e.status AS outbox_status, e.attempts AS outbox_attempts
       FROM orders o
       LEFT JOIN outbox_events e ON e.order_id = o.order_id
      WHERE o.order_id = $1`,
    [orderId]
  );

  if (result.rowCount === 0) {
    return sendJson(res, 404, { error: "ORDER_NOT_FOUND", orderId, traceId: traceCtx.traceId }, headers);
  }

  return sendJson(res, 200, Object.assign(result.rows[0], { traceId: traceCtx.traceId }), headers);
}

async function handleCreateOrder(req, res, traceCtx) {
  const baseHeaders = { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId };
  const rawKey = req.headers["idempotency-key"];
  const idempotencyKey = typeof rawKey === "string" ? rawKey.trim() : "";

  if (!idempotencyKey || idempotencyKey.length > 128) {
    return sendJson(
      res,
      400,
      {
        error: "MISSING_IDEMPOTENCY_KEY",
        message: "Header 'Idempotency-Key' is required (1-128 chars).",
        traceId: traceCtx.traceId
      },
      baseHeaders
    );
  }

  let rawBody;
  try {
    rawBody = await readJsonBody(req);
  } catch (err) {
    return sendJson(
      res,
      err.statusCode || 400,
      { error: err.code || "INVALID_JSON", message: err.message, traceId: traceCtx.traceId },
      baseHeaders
    );
  }

  const parsed = domain.canonicalizeOrderPayload(rawBody);
  if (!parsed.ok) {
    return sendJson(
      res,
      400,
      { error: parsed.errorCode, message: parsed.message, traceId: traceCtx.traceId },
      baseHeaders
    );
  }

  const payloadHash = crypto.createHash("sha256").update(parsed.canonical).digest("hex");
  const client = await pool.connect();
  let inTx = false;

  try {
    await client.query("BEGIN");
    inTx = true;

    // Serialize concurrent retries carrying the identical Idempotency-Key
    await client.query("SELECT pg_advisory_xact_lock(hashtext($1))", [idempotencyKey]);

    const existingRes = await client.query(
      "SELECT payload_hash, status_code, response_body FROM idempotency_keys WHERE idempotency_key = $1",
      [idempotencyKey]
    );

    const existing = existingRes.rowCount > 0
      ? {
          payloadHash: existingRes.rows[0].payload_hash,
          statusCode: existingRes.rows[0].status_code,
          responseBody: existingRes.rows[0].response_body
        }
      : null;

    const idemDecision = domain.evaluateIdempotency(existing, payloadHash);
    if (idemDecision.action === "REPLAY") {
      await client.query("COMMIT");
      inTx = false;
      log.info("order_idempotent_replay", {
        traceId: traceCtx.traceId,
        idempotencyKey,
        statusCode: idemDecision.statusCode
      });
      return sendJson(
        res,
        idemDecision.statusCode,
        idemDecision.responseBody,
        Object.assign({ "x-idempotent-replay": "true" }, baseHeaders)
      );
    }

    if (idemDecision.action === "CONFLICT") {
      await client.query("ROLLBACK");
      inTx = false;
      log.warn("order_idempotency_conflict", { traceId: traceCtx.traceId, idempotencyKey });
      return sendJson(
        res,
        422,
        {
          error: "IDEMPOTENCY_CONFLICT",
          message: "Idempotency-Key was already used with a different request payload.",
          traceId: traceCtx.traceId
        },
        baseHeaders
      );
    }

    // Optimistic locking loop on inventory.version + conditional available >= qty guard
    let updatedInventory = null;
    for (let attempt = 1; attempt <= MAX_OPTIMISTIC_RETRIES; attempt++) {
      const invRes = await client.query(
        "SELECT sku, available, version FROM inventory WHERE sku = $1",
        [parsed.sku]
      );
      const currentRow = invRes.rowCount > 0
        ? {
            sku: invRes.rows[0].sku,
            available: Number(invRes.rows[0].available),
            version: Number(invRes.rows[0].version)
          }
        : null;

      const stockCheck = domain.computeOptimisticStockUpdate(currentRow, parsed.qty);
      if (!stockCheck.ok) {
        if (stockCheck.statusCode === 409) {
          const rejectBody = {
            error: stockCheck.errorCode,
            sku: parsed.sku,
            qty: parsed.qty,
            traceId: traceCtx.traceId
          };
          await client.query(
            `INSERT INTO idempotency_keys (idempotency_key, payload_hash, status_code, order_id, response_body)
             VALUES ($1, $2, 409, NULL, $3::jsonb)`,
            [idempotencyKey, payloadHash, JSON.stringify(rejectBody)]
          );
          await client.query("COMMIT");
          inTx = false;
          log.warn("order_insufficient_stock", {
            traceId: traceCtx.traceId,
            sku: parsed.sku,
            qty: parsed.qty
          });
          return sendJson(res, 409, rejectBody, baseHeaders);
        }

        await client.query("ROLLBACK");
        inTx = false;
        return sendJson(
          res,
          stockCheck.statusCode,
          { error: stockCheck.errorCode, sku: parsed.sku, traceId: traceCtx.traceId },
          baseHeaders
        );
      }

      const updateRes = await client.query(
        `UPDATE inventory
            SET available = available - $1,
                version = version + 1,
                updated_at = now()
          WHERE sku = $2
            AND version = $3
            AND available >= $1
         RETURNING sku, available, version`,
        [parsed.qty, parsed.sku, stockCheck.expectedVersion]
      );

      if (updateRes.rowCount === 1) {
        updatedInventory = updateRes.rows[0];
        break;
      }

      log.warn("optimistic_lock_retry", {
        traceId: traceCtx.traceId,
        sku: parsed.sku,
        attempt
      });
    }

    if (!updatedInventory) {
      await client.query("ROLLBACK");
      inTx = false;
      return sendJson(
        res,
        409,
        {
          error: "OPTIMISTIC_LOCK_CONFLICT",
          message: "Concurrent inventory modification detected; safe to retry.",
          traceId: traceCtx.traceId
        },
        baseHeaders
      );
    }

    const orderId = crypto.randomUUID();
    const eventId = crypto.randomUUID();

    await client.query(
      `INSERT INTO orders (order_id, sku, qty, status, trace_id)
       VALUES ($1, $2, $3, 'PENDING', $4)`,
      [orderId, parsed.sku, parsed.qty, traceCtx.traceId]
    );

    const outboxPayload = {
      eventId,
      orderId,
      sku: parsed.sku,
      qty: parsed.qty,
      simulateFailure: parsed.simulateFailure,
      traceId: traceCtx.traceId,
      traceparent: traceCtx.traceparent
    };

    await client.query(
      `INSERT INTO outbox_events (event_id, order_id, event_type, payload, status)
       VALUES ($1, $2, 'ORDER_CREATED', $3::jsonb, 'PENDING')`,
      [eventId, orderId, JSON.stringify(outboxPayload)]
    );

    const responseBody = {
      orderId,
      eventId,
      sku: parsed.sku,
      qty: parsed.qty,
      status: "PENDING",
      inventoryVersion: Number(updatedInventory.version),
      remainingStock: Number(updatedInventory.available),
      traceId: traceCtx.traceId
    };

    await client.query(
      `INSERT INTO idempotency_keys (idempotency_key, payload_hash, status_code, order_id, response_body)
       VALUES ($1, $2, 201, $3, $4::jsonb)`,
      [idempotencyKey, payloadHash, orderId, JSON.stringify(responseBody)]
    );

    await client.query("COMMIT");
    inTx = false;

    if (redis && redis.status === "ready") {
      redis
        .del("inventory:" + parsed.sku)
        .catch((err) => log.warn("redis_del_failed", { traceId: traceCtx.traceId, error: err.message }));
    }

    log.info("order_created", {
      traceId: traceCtx.traceId,
      orderId,
      eventId,
      sku: parsed.sku,
      qty: parsed.qty,
      remainingStock: Number(updatedInventory.available)
    });

    return sendJson(res, 201, responseBody, baseHeaders);
  } catch (err) {
    if (inTx) {
      await client.query("ROLLBACK").catch(() => {});
    }
    log.error("order_create_failed", { traceId: traceCtx.traceId, error: err.message });
    return sendJson(
      res,
      500,
      { error: "INTERNAL_ERROR", message: "Order transaction failed.", traceId: traceCtx.traceId },
      baseHeaders
    );
  } finally {
    client.release();
  }
}

let isShuttingDown = false;
let activeRequests = 0;
const trackedSockets = new Set();

const server = http.createServer(async (req, res) => {
  activeRequests += 1;
  if (req.socket) {
    req.socket._isIdle = false;
  }

  const startedAt = Date.now();
  const traceCtx = domain.parseOrCreateTraceContext(req.headers["traceparent"]);

  res.on("finish", () => {
    activeRequests -= 1;
    if (req.socket) {
      req.socket._isIdle = true;
      if (isShuttingDown) {
        req.socket.end();
      }
    }
    log.info("http_request", {
      traceId: traceCtx.traceId,
      method: req.method,
      path: req.url,
      statusCode: res.statusCode,
      durationMs: Date.now() - startedAt
    });
  });

  try {
    const parsedUrl = new URL(req.url || "/", "http://localhost");
    const pathname = parsedUrl.pathname;

    if (req.method === "GET" && pathname === "/healthz") {
      if (isShuttingDown) {
        return sendJson(
          res,
          503,
          { status: "shutting_down", traceId: traceCtx.traceId },
          { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
        );
      }
      return sendJson(
        res,
        200,
        { status: "ok", traceId: traceCtx.traceId },
        { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
      );
    }

    if (req.method === "GET" && pathname === "/readyz") {
      return await handleReadyz(res, traceCtx, isShuttingDown);
    }

    if (isShuttingDown) {
      return sendJson(
        res,
        503,
        { error: "SERVER_SHUTTING_DOWN", traceId: traceCtx.traceId },
        { connection: "close", traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
      );
    }

    if (req.method === "POST" && (pathname === "/orders" || pathname === "/v1/orders")) {
      return await handleCreateOrder(req, res, traceCtx);
    }

    if (req.method === "GET" && pathname.startsWith("/inventory/")) {
      const sku = decodeURIComponent(pathname.slice("/inventory/".length));
      if (!sku) {
        return sendJson(res, 400, { error: "MISSING_SKU", traceId: traceCtx.traceId });
      }
      return await handleGetInventory(sku, res, traceCtx);
    }

    if (req.method === "GET" && pathname.startsWith("/orders/")) {
      const orderId = decodeURIComponent(pathname.slice("/orders/".length));
      return await handleGetOrder(orderId, res, traceCtx);
    }

    return sendJson(
      res,
      404,
      { error: "NOT_FOUND", path: pathname, traceId: traceCtx.traceId },
      { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
    );
  } catch (err) {
    log.error("unhandled_request_error", { traceId: traceCtx.traceId, error: err.message });
    if (!res.headersSent) {
      sendJson(
        res,
        500,
        { error: "INTERNAL_ERROR", traceId: traceCtx.traceId },
        { traceparent: traceCtx.traceparent, "x-trace-id": traceCtx.traceId }
      );
    }
  }
});

// Must exceed upstream proxy/ALB idle timeout (Lesson 0001)
server.keepAliveTimeout = 65000;
server.headersTimeout = 66000;
server.requestTimeout = 30000;

server.on("connection", (socket) => {
  socket._isIdle = true;
  trackedSockets.add(socket);
  socket.on("close", () => {
    trackedSockets.delete(socket);
  });
});

async function shutdown(signal) {
  if (isShuttingDown) {
    return;
  }
  isShuttingDown = true;
  log.info("shutdown_started", { signal, activeRequests, openSockets: trackedSockets.size });

  // Immediately close idle keep-alive sockets so clients reconnect to healthy pods
  for (const socket of trackedSockets) {
    if (socket._isIdle) {
      socket.destroy();
    }
  }

  const forceTimer = setTimeout(() => {
    log.warn("shutdown_grace_expired", { remainingSockets: trackedSockets.size, activeRequests });
    for (const socket of trackedSockets) {
      socket.destroy();
    }
    process.exit(1);
  }, SHUTDOWN_GRACE_MS);
  forceTimer.unref();

  server.close(async (closeErr) => {
    clearTimeout(forceTimer);
    if (closeErr) {
      log.error("http_server_close_error", { error: closeErr.message });
    }
    try {
      await pool.end();
      if (redis && redis.status !== "end" && redis.status !== "wait") {
        await redis.quit();
      }
    } catch (cleanupErr) {
      log.error("dependency_shutdown_error", { error: cleanupErr.message });
    }
    log.info("shutdown_complete", { signal });
    process.exit(0);
  });
}

process.on("SIGTERM", () => shutdown("SIGTERM"));
process.on("SIGINT", () => shutdown("SIGINT"));

server.listen(PORT, "0.0.0.0", async () => {
  if (redis) {
    redis.connect().catch((err) => {
      log.warn("redis_initial_connect_failed", { error: err.message });
    });
  }
  log.info("server_listening", {
    port: PORT,
    keepAliveTimeout: server.keepAliveTimeout,
    headersTimeout: server.headersTimeout
  });
});
