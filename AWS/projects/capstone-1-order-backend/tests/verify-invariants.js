/**
 * Standalone domain & invariant verification suite.
 * Runs with zero external database dependencies under either Node.js or JavaScriptCore (jsc):
 *   - node tests/verify-invariants.js
 *   - jsc tests/verify-invariants.js
 */
(function () {
  "use strict";

  const log =
    typeof console !== "undefined" && typeof console.log === "function"
      ? console.log.bind(console)
      : print;

  function exitWith(code) {
    if (typeof process !== "undefined" && typeof process.exit === "function") {
      process.exit(code);
    }
    if (typeof quit === "function") {
      quit(code);
    }
    throw new Error("Exit " + code);
  }

  let domain;
  if (typeof require === "function") {
    domain = require("../src/domain");
  } else if (typeof load === "function") {
    try {
      load("src/domain.js");
    } catch (_err) {
      load("../src/domain.js");
    }
    domain = globalThis.OrderDomain;
  }

  if (!domain) {
    log("FAIL: Could not load src/domain.js");
    exitWith(1);
  }

  let passed = 0;
  let failed = 0;

  function assert(condition, message) {
    if (!condition) {
      throw new Error("Assertion failed: " + message);
    }
  }

  function assertEqual(actual, expected, message) {
    if (actual !== expected) {
      throw new Error(
        message + " (expected=" + JSON.stringify(expected) + ", actual=" + JSON.stringify(actual) + ")"
      );
    }
  }

  function runTest(name, fn) {
    try {
      fn();
      passed += 1;
      log("[PASS] " + name);
    } catch (err) {
      failed += 1;
      log("[FAIL] " + name + " -> " + err.message);
    }
  }

  // 1. W3C traceparent parsing & generation
  runTest("W3C traceparent inherits valid traceId and mints new child spanId", () => {
    const incoming = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
    const ctx = domain.parseOrCreateTraceContext(incoming, () => 0.5);
    assertEqual(ctx.inherited, true, "inherited flag");
    assertEqual(ctx.traceId, "4bf92f3577b34da6a3ce929d0e0e4736", "traceId preserved");
    assertEqual(ctx.parentSpanId, "00f067aa0ba902b7", "parentSpanId preserved");
    assert(ctx.spanId !== ctx.parentSpanId, "new child spanId generated");
    assert(/^00-4bf92f3577b34da6a3ce929d0e0e4736-[0-9a-f]{16}-01$/.test(ctx.traceparent), "valid traceparent");
  });

  runTest("Malformed or missing traceparent generates valid non-zero traceId", () => {
    const ctxMissing = domain.parseOrCreateTraceContext(undefined, () => 0);
    assertEqual(ctxMissing.inherited, false, "not inherited");
    assertEqual(ctxMissing.traceId.length, 32, "32 hex traceId");
    assert(ctxMissing.traceId !== "00000000000000000000000000000000", "non-zero traceId");

    const ctxAllZero = domain.parseOrCreateTraceContext("00-00000000000000000000000000000000-00f067aa0ba902b7-01");
    assertEqual(ctxAllZero.inherited, false, "all-zero traceId rejected");
  });

  // 2. Canonical payload validation
  runTest("Order payload canonicalization normalizes key ordering and validates bounds", () => {
    const a = domain.canonicalizeOrderPayload({ sku: " SKU-KB-01 ", qty: 2 });
    const b = domain.canonicalizeOrderPayload({ qty: 2, sku: "SKU-KB-01" });
    assertEqual(a.ok, true, "payload A valid");
    assertEqual(b.ok, true, "payload B valid");
    assertEqual(a.canonical, b.canonical, "canonical representation matches across key order");

    const badQty = domain.canonicalizeOrderPayload({ sku: "SKU-KB-01", qty: 0 });
    assertEqual(badQty.ok, false, "zero qty rejected");
    assertEqual(badQty.errorCode, "INVALID_QUANTITY", "INVALID_QUANTITY code");

    const floatQty = domain.canonicalizeOrderPayload({ sku: "SKU-KB-01", qty: 1.5 });
    assertEqual(floatQty.ok, false, "fractional qty rejected");

    const emptySku = domain.canonicalizeOrderPayload({ sku: "   ", qty: 1 });
    assertEqual(emptySku.ok, false, "blank sku rejected");
  });

  // 3. Idempotency state machine
  runTest("Idempotency evaluation distinguishes new, replay, and 422 payload conflict", () => {
    assertEqual(domain.evaluateIdempotency(null, "hash-1").action, "PROCEED", "new key proceeds");

    const record = { payloadHash: "hash-1", statusCode: 201, responseBody: { orderId: "o-1" } };
    const replay = domain.evaluateIdempotency(record, "hash-1");
    assertEqual(replay.action, "REPLAY", "matching hash replays");
    assertEqual(replay.statusCode, 201, "replays original status");
    assertEqual(replay.responseBody.orderId, "o-1", "replays original body");

    const conflict = domain.evaluateIdempotency(record, "hash-2");
    assertEqual(conflict.action, "CONFLICT", "mismatched hash conflicts");
    assertEqual(conflict.statusCode, 422, "422 Unprocessable Entity");
    assertEqual(conflict.errorCode, "IDEMPOTENCY_CONFLICT", "IDEMPOTENCY_CONFLICT code");
  });

  // 4. Optimistic locking & stock guards
  runTest("Optimistic stock update enforces bounds and increments version", () => {
    const missing = domain.computeOptimisticStockUpdate(null, 1);
    assertEqual(missing.ok, false, "missing SKU fails");
    assertEqual(missing.statusCode, 404, "404 for missing SKU");

    const low = domain.computeOptimisticStockUpdate({ sku: "SKU-1", available: 2, version: 4 }, 3);
    assertEqual(low.ok, false, "insufficient stock fails");
    assertEqual(low.statusCode, 409, "409 for insufficient stock");
    assertEqual(low.errorCode, "INSUFFICIENT_STOCK", "INSUFFICIENT_STOCK code");

    const ok = domain.computeOptimisticStockUpdate({ sku: "SKU-1", available: 5, version: 4 }, 3);
    assertEqual(ok.ok, true, "sufficient stock succeeds");
    assertEqual(ok.nextAvailable, 2, "nextAvailable decremented");
    assertEqual(ok.expectedVersion, 4, "expectedVersion preserved");
    assertEqual(ok.nextVersion, 5, "version incremented");
  });

  // 5. Exponential backoff + full jitter
  runTest("Exponential backoff with full jitter stays within [0, min(maxMs, baseMs * 2^(attempt-1)))", () => {
    const baseMs = 200;
    const maxMs = 1000;

    // rng = 0 -> lower bound 0
    assertEqual(domain.computeBackoffWithJitterMs(1, baseMs, maxMs, () => 0), 0, "lower bound 0");
    // rng ~ 1 -> attempt 1 cap 200 -> 199
    assertEqual(domain.computeBackoffWithJitterMs(1, baseMs, maxMs, () => 0.9999999), 199, "attempt 1 upper bound");
    // attempt 2 cap 400, rng = 0.5 -> 200
    assertEqual(domain.computeBackoffWithJitterMs(2, baseMs, maxMs, () => 0.5), 200, "attempt 2 midpoint");
    // attempt 4 cap min(1000, 1600) = 1000 -> clamped to maxMs
    const highAttempt = domain.computeBackoffWithJitterMs(4, baseMs, maxMs, () => 0.9999999);
    assert(highAttempt < maxMs && highAttempt >= 0, "attempt 4 respects maxMs cap");
  });

  // 6. Worker failure decision & DLQ boundary
  runTest("Worker retries with backoff below maxAttempts and routes to DLQ at maxAttempts", () => {
    const retry = domain.decideWorkerFailureAction(2, 5, 200, 15000, () => 0.5);
    assertEqual(retry.nextStatus, "PENDING", "remains PENDING before maxAttempts");
    assertEqual(retry.orderStatus, "PENDING", "order remains PENDING");
    assert(retry.delayMs > 0, "backoff delay scheduled");

    const dlq = domain.decideWorkerFailureAction(5, 5, 200, 15000, () => 0.5);
    assertEqual(dlq.nextStatus, "DLQ", "moves to DLQ at maxAttempts");
    assertEqual(dlq.orderStatus, "FAILED_DLQ", "order marked FAILED_DLQ");
    assertEqual(dlq.delayMs, 0, "no further retry delay");
  });

  // 7. Full pipeline invariant verification (Idempotency + Optimistic Locking + Outbox + Worker Dedup + DLQ + Conservation)
  runTest("End-to-end pipeline preserves stock conservation, deduplication, and DLQ isolation", () => {
    const ops = [
      // Order o1 reserves 3 of SKU-A (stock 5 -> 2, version 1 -> 2)
      { type: "API_ORDER", orderId: "o1", idempotencyKey: "k1", payloadHash: "h1", sku: "SKU-A", qty: 3, expectedVersion: 1 },
      // Network retry with same key + same hash -> IDEMPOTENT_REPLAY (no extra deduction)
      { type: "API_ORDER", orderId: "o1-retry", idempotencyKey: "k1", payloadHash: "h1", sku: "SKU-A", qty: 3 },
      // Buggy client reuses key k1 with different hash -> IDEMPOTENCY_CONFLICT
      { type: "API_ORDER", orderId: "o1-bad", idempotencyKey: "k1", payloadHash: "h-other", sku: "SKU-A", qty: 1 },
      // Stale reader tries expectedVersion=1 -> OPTIMISTIC_LOCK_CONFLICT
      { type: "API_ORDER", orderId: "o2-stale", idempotencyKey: "k2-stale", payloadHash: "h2", sku: "SKU-A", qty: 1, expectedVersion: 1 },
      // Oversell attempt (qty 3 when remaining is 2) -> INSUFFICIENT_STOCK
      { type: "API_ORDER", orderId: "o2-over", idempotencyKey: "k2-over", payloadHash: "h2-over", sku: "SKU-A", qty: 3 },
      // Valid order o2 takes remaining 2 (stock 2 -> 0, version 2 -> 3)
      { type: "API_ORDER", orderId: "o2", idempotencyKey: "k2", payloadHash: "h2", sku: "SKU-A", qty: 2, expectedVersion: 2 },
      // Poison order o3 on SKU-B (stock 4 -> 3)
      { type: "API_ORDER", orderId: "o3", idempotencyKey: "k3", payloadHash: "h3", sku: "SKU-B", qty: 1 },

      // Worker delivers evt-o1 twice (crash before ack simulation) -> PROCESSED then DUPLICATE_ACK
      { type: "WORKER_DELIVER", eventId: "evt-o1", shouldFail: false },
      { type: "WORKER_DELIVER", eventId: "evt-o1", shouldFail: false },
      // Worker delivers evt-o2 after 1 transient failure -> RETRY_SCHEDULED then PROCESSED
      { type: "WORKER_DELIVER", eventId: "evt-o2", shouldFail: true },
      { type: "WORKER_DELIVER", eventId: "evt-o2", shouldFail: false },
      // Worker fails evt-o3 maxAttempts (3) times -> MOVED_TO_DLQ, and extra delivery -> ALREADY_DLQ
      { type: "WORKER_DELIVER", eventId: "evt-o3", shouldFail: true },
      { type: "WORKER_DELIVER", eventId: "evt-o3", shouldFail: true },
      { type: "WORKER_DELIVER", eventId: "evt-o3", shouldFail: true },
      { type: "WORKER_DELIVER", eventId: "evt-o3", shouldFail: false }
    ];

    const res = domain.verifyOrderPipeline(
      ops,
      { "SKU-A": 5, "SKU-B": 4 },
      { maxAttempts: 3, baseBackoffMs: 100, maxBackoffMs: 1000, rng: () => 0.5 }
    );

    const expectedOutcomes = [
      "CREATED",
      "IDEMPOTENT_REPLAY",
      "IDEMPOTENCY_CONFLICT",
      "OPTIMISTIC_LOCK_CONFLICT",
      "INSUFFICIENT_STOCK",
      "CREATED",
      "CREATED",
      "PROCESSED",
      "DUPLICATE_ACK",
      "RETRY_SCHEDULED",
      "PROCESSED",
      "RETRY_SCHEDULED",
      "RETRY_SCHEDULED",
      "MOVED_TO_DLQ",
      "ALREADY_DLQ"
    ];

    assertEqual(
      JSON.stringify(res.outcomes),
      JSON.stringify(expectedOutcomes),
      "pipeline outcomes sequence"
    );
    assertEqual(res.remainingStock["SKU-A"], 0, "SKU-A remainingStock");
    assertEqual(res.remainingStock["SKU-B"], 3, "SKU-B remainingStock");
    assertEqual(res.inventory["SKU-A"].version, 3, "SKU-A optimistic version");
    assertEqual(res.ledgerCommits, 2, "exactly 2 confirmed ledger commits");
    assertEqual(res.processedCount, 2, "exactly 2 unique processed events");
    assertEqual(res.dlq.length, 1, "1 poison event in DLQ");
    assertEqual(res.dlq[0], "evt-o3", "evt-o3 quarantined in DLQ");
    assertEqual(res.invariants.noNegativeStock, true, "noNegativeStock invariant");
    assertEqual(res.invariants.stockConserved, true, "stockConserved invariant");
    assertEqual(res.invariants.ledgerMatchesConfirmed, true, "ledgerMatchesConfirmed invariant");
    assertEqual(res.invariants.dlqSafe, true, "dlqSafe invariant");
    assertEqual(res.invariants.valid, true, "overall invariants valid");
  });

  log("\nSummary: " + passed + " passed, " + failed + " failed.");
  exitWith(failed === 0 ? 0 : 1);
})();
