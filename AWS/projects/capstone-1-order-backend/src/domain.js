/**
 * Pure, dependency-free domain logic shared by the API server, async worker,
 * and standalone invariant verification suite (compatible with both Node.js and jsc).
 */
(function (root) {
  "use strict";

  const TRACEPARENT_RE = /^00-([0-9a-f]{32})-([0-9a-f]{16})-([0-9a-f]{2})$/i;
  const ALL_ZERO_32 = "00000000000000000000000000000000";
  const ALL_ZERO_16 = "0000000000000000";

  function randomHex(length, rng) {
    const rand = typeof rng === "function" ? rng : Math.random;
    let out = "";
    for (let i = 0; i < length; i++) {
      out += Math.floor(rand() * 16).toString(16);
    }
    return out;
  }

  /**
   * Parses an incoming W3C traceparent header or generates a fresh trace context.
   */
  function parseOrCreateTraceContext(traceparentHeader, rng) {
    if (typeof traceparentHeader === "string") {
      const match = TRACEPARENT_RE.exec(traceparentHeader.trim());
      if (match) {
        const traceId = match[1].toLowerCase();
        const parentSpanId = match[2].toLowerCase();
        const flags = match[3].toLowerCase();
        if (traceId !== ALL_ZERO_32 && parentSpanId !== ALL_ZERO_16) {
          const spanId = randomHex(16, rng);
          return {
            traceId,
            spanId,
            parentSpanId,
            traceparent: "00-" + traceId + "-" + spanId + "-" + flags,
            inherited: true
          };
        }
      }
    }

    let traceId = randomHex(32, rng);
    if (traceId === ALL_ZERO_32) {
      traceId = "10000000000000000000000000000001";
    }
    let spanId = randomHex(16, rng);
    if (spanId === ALL_ZERO_16) {
      spanId = "1000000000000001";
    }
    return {
      traceId,
      spanId,
      parentSpanId: null,
      traceparent: "00-" + traceId + "-" + spanId + "-01",
      inherited: false
    };
  }

  /**
   * Validates and canonicalizes an order creation request body so equivalent
   * JSON payloads with different key order produce an identical canonical string.
   */
  function canonicalizeOrderPayload(body) {
    if (!body || typeof body !== "object" || Array.isArray(body)) {
      return { ok: false, errorCode: "INVALID_BODY", message: "Request body must be a JSON object." };
    }
    const rawSku = typeof body.sku === "string" ? body.sku.trim() : "";
    if (!rawSku || rawSku.length > 128) {
      return { ok: false, errorCode: "INVALID_SKU", message: "Field 'sku' must be a non-empty string (max 128 chars)." };
    }
    if (typeof body.qty !== "number" || !Number.isInteger(body.qty) || body.qty <= 0 || body.qty > 100000) {
      return { ok: false, errorCode: "INVALID_QUANTITY", message: "Field 'qty' must be a positive integer <= 100000." };
    }

    const simulateFailure = body.simulateFailure === true;
    const canonical = simulateFailure
      ? JSON.stringify({ qty: body.qty, simulateFailure: true, sku: rawSku })
      : JSON.stringify({ qty: body.qty, sku: rawSku });

    return {
      ok: true,
      sku: rawSku,
      qty: body.qty,
      simulateFailure,
      canonical
    };
  }

  /**
   * Evaluates an existing idempotency_keys record against the incoming payload hash.
   */
  function evaluateIdempotency(existingRecord, payloadHash) {
    if (!existingRecord) {
      return { action: "PROCEED" };
    }
    if (existingRecord.payloadHash === payloadHash) {
      return {
        action: "REPLAY",
        statusCode: existingRecord.statusCode,
        responseBody: existingRecord.responseBody
      };
    }
    return {
      action: "CONFLICT",
      statusCode: 422,
      errorCode: "IDEMPOTENCY_CONFLICT"
    };
  }

  /**
   * Computes the next inventory state for an optimistic-locking conditional update.
   */
  function computeOptimisticStockUpdate(currentRow, qty) {
    if (!currentRow) {
      return { ok: false, statusCode: 404, errorCode: "SKU_NOT_FOUND" };
    }
    if (typeof qty !== "number" || !Number.isInteger(qty) || qty <= 0) {
      return { ok: false, statusCode: 400, errorCode: "INVALID_QUANTITY" };
    }
    if (currentRow.available < qty) {
      return { ok: false, statusCode: 409, errorCode: "INSUFFICIENT_STOCK" };
    }
    return {
      ok: true,
      nextAvailable: currentRow.available - qty,
      expectedVersion: currentRow.version,
      nextVersion: currentRow.version + 1
    };
  }

  /**
   * Exponential backoff with AWS-style full jitter: random in [0, min(maxMs, baseMs * 2^(attempt-1))).
   */
  function computeBackoffWithJitterMs(attempt, baseMs, maxMs, rng) {
    const safeAttempt = Math.max(1, Math.floor(attempt || 1));
    const base = typeof baseMs === "number" && baseMs > 0 ? baseMs : 200;
    const capMax = typeof maxMs === "number" && maxMs >= base ? maxMs : 15000;
    const rand = typeof rng === "function" ? rng : Math.random;

    const expCap = Math.min(capMax, base * Math.pow(2, safeAttempt - 1));
    const sample = Math.min(0.999999999, Math.max(0, rand()));
    return Math.floor(sample * expCap);
  }

  /**
   * Determines whether a failed outbox delivery should be retried with backoff
   * or quarantined into the Dead Letter Queue (DLQ).
   */
  function decideWorkerFailureAction(attemptsAfterIncrement, maxAttempts, baseMs, maxMs, rng) {
    const limit = typeof maxAttempts === "number" && maxAttempts >= 1 ? maxAttempts : 5;
    if (attemptsAfterIncrement >= limit) {
      return {
        nextStatus: "DLQ",
        orderStatus: "FAILED_DLQ",
        delayMs: 0
      };
    }
    return {
      nextStatus: "PENDING",
      orderStatus: "PENDING",
      delayMs: computeBackoffWithJitterMs(attemptsAfterIncrement, baseMs, maxMs, rng)
    };
  }

  /**
   * Pure in-memory reference model of the Order API + Outbox + Worker pipeline.
   * Used to prove conservation, idempotency, optimistic locking, and DLQ invariants
   * deterministically with zero external database dependencies.
   */
  function verifyOrderPipeline(operations, initialStock, config) {
    const cfg = config || {};
    const maxAttempts = typeof cfg.maxAttempts === "number" && cfg.maxAttempts >= 1 ? cfg.maxAttempts : 5;
    const baseBackoffMs = typeof cfg.baseBackoffMs === "number" ? cfg.baseBackoffMs : 200;
    const maxBackoffMs = typeof cfg.maxBackoffMs === "number" ? cfg.maxBackoffMs : 15000;
    const rng = typeof cfg.rng === "function" ? cfg.rng : Math.random;

    const inventory = {};
    const initialTotals = {};
    const skus = Object.keys(initialStock || {});
    for (let i = 0; i < skus.length; i++) {
      const sku = skus[i];
      const avail = initialStock[sku];
      inventory[sku] = { sku, available: avail, version: 1 };
      initialTotals[sku] = avail;
    }

    const outcomes = [];
    const orders = {};
    const outbox = {};
    const idempotencyStore = {};
    const processedEvents = new Set();
    const dlq = [];
    let ledgerCommits = 0;

    for (let i = 0; i < operations.length; i++) {
      const op = operations[i];

      if (op.type === "API_ORDER") {
        const existing = idempotencyStore[op.idempotencyKey];
        const decision = evaluateIdempotency(existing, op.payloadHash);
        if (decision.action === "REPLAY") {
          outcomes.push("IDEMPOTENT_REPLAY");
          continue;
        }
        if (decision.action === "CONFLICT") {
          outcomes.push("IDEMPOTENCY_CONFLICT");
          continue;
        }

        const currentRow = inventory[op.sku];
        if (typeof op.expectedVersion === "number" && currentRow && op.expectedVersion !== currentRow.version) {
          outcomes.push("OPTIMISTIC_LOCK_CONFLICT");
          continue;
        }

        const stockStep = computeOptimisticStockUpdate(currentRow, op.qty);
        if (!stockStep.ok) {
          idempotencyStore[op.idempotencyKey] = {
            payloadHash: op.payloadHash,
            statusCode: stockStep.statusCode,
            responseBody: { error: stockStep.errorCode }
          };
          outcomes.push(stockStep.errorCode);
          continue;
        }

        inventory[op.sku] = {
          sku: op.sku,
          available: stockStep.nextAvailable,
          version: stockStep.nextVersion
        };

        orders[op.orderId] = {
          orderId: op.orderId,
          sku: op.sku,
          qty: op.qty,
          status: "PENDING"
        };

        const eventId = "evt-" + op.orderId;
        outbox[eventId] = {
          eventId,
          orderId: op.orderId,
          sku: op.sku,
          qty: op.qty,
          attempts: 0,
          state: "PENDING",
          lastDelayMs: 0
        };

        idempotencyStore[op.idempotencyKey] = {
          payloadHash: op.payloadHash,
          statusCode: 201,
          responseBody: { orderId: op.orderId, status: "PENDING" }
        };
        outcomes.push("CREATED");
      } else if (op.type === "WORKER_DELIVER") {
        const evt = outbox[op.eventId];
        if (!evt) {
          outcomes.push("MISSING_EVENT");
          continue;
        }
        if (processedEvents.has(op.eventId)) {
          outcomes.push("DUPLICATE_ACK");
          continue;
        }
        if (evt.state === "DLQ") {
          outcomes.push("ALREADY_DLQ");
          continue;
        }

        evt.attempts += 1;
        if (!op.shouldFail) {
          evt.state = "PROCESSED";
          processedEvents.add(op.eventId);
          orders[evt.orderId].status = "CONFIRMED";
          ledgerCommits += 1;
          outcomes.push("PROCESSED");
        } else {
          const failureAction = decideWorkerFailureAction(
            evt.attempts,
            maxAttempts,
            baseBackoffMs,
            maxBackoffMs,
            rng
          );
          evt.state = failureAction.nextStatus;
          evt.lastDelayMs = failureAction.delayMs;
          orders[evt.orderId].status = failureAction.orderStatus;
          if (failureAction.nextStatus === "DLQ") {
            dlq.push(evt.eventId);
            outcomes.push("MOVED_TO_DLQ");
          } else {
            outcomes.push("RETRY_SCHEDULED");
          }
        }
      } else {
        throw new Error("Unknown operation type: " + op.type);
      }
    }

    const remainingStock = {};
    const reservedBySku = {};
    const orderList = Object.values(orders);
    for (let i = 0; i < orderList.length; i++) {
      const ord = orderList[i];
      reservedBySku[ord.sku] = (reservedBySku[ord.sku] || 0) + ord.qty;
    }

    let noNegativeStock = true;
    let stockConserved = true;
    const invKeys = Object.keys(inventory);
    for (let i = 0; i < invKeys.length; i++) {
      const sku = invKeys[i];
      const rem = inventory[sku].available;
      remainingStock[sku] = rem;
      if (rem < 0) {
        noNegativeStock = false;
      }
      if (initialTotals[sku] !== rem + (reservedBySku[sku] || 0)) {
        stockConserved = false;
      }
    }

    let confirmedCount = 0;
    let dlqOrderCount = 0;
    for (let i = 0; i < orderList.length; i++) {
      if (orderList[i].status === "CONFIRMED") confirmedCount += 1;
      if (orderList[i].status === "FAILED_DLQ") dlqOrderCount += 1;
    }

    const ledgerMatchesConfirmed =
      confirmedCount === ledgerCommits && processedEvents.size === ledgerCommits;
    const dlqSafe = dlq.length === dlqOrderCount;

    const invariants = {
      noNegativeStock,
      stockConserved,
      ledgerMatchesConfirmed,
      dlqSafe,
      valid: noNegativeStock && stockConserved && ledgerMatchesConfirmed && dlqSafe
    };

    return {
      outcomes,
      remainingStock,
      inventory,
      orders,
      outbox,
      processedCount: processedEvents.size,
      ledgerCommits,
      dlq,
      invariants
    };
  }

  const exportsObj = {
    parseOrCreateTraceContext,
    canonicalizeOrderPayload,
    evaluateIdempotency,
    computeOptimisticStockUpdate,
    computeBackoffWithJitterMs,
    decideWorkerFailureAction,
    verifyOrderPipeline
  };

  if (typeof module !== "undefined" && module.exports) {
    module.exports = exportsObj;
  } else {
    root.OrderDomain = exportsObj;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
