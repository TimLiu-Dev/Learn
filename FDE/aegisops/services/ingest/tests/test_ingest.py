"""Unit tests for AegisOps ingestion, HMAC verification, PII scrubbing, and monotonic upserts."""

import hashlib
import hmac
import json
import unittest

from services.ingest.hmac_guard import verify_webhook_signature
from services.ingest.triage import ingest_webhook_event


def sign_payload(secret: bytes, ts: int, body: bytes) -> str:
    return "v1=" + hmac.new(secret, f"{ts}.".encode("utf-8") + body, hashlib.sha256).hexdigest()


class IngestPipelineTest(unittest.TestCase):
    def setUp(self):
        self.cfg = {
            "tenant_id": "acme_logistics",
            "field_map": {
                "order_id": "erp_ref",
                "amount": "gross_usd",
                "status": "fulfillment_state",
                "updated_at_ms": "modified_epoch_ms",
            },
        }

    def test_hmac_rejects_tampered_and_stale_webhooks(self):
        secret = b"top_secret_key"
        body = b'{"erp_ref":"ORD-1"}'
        sig = sign_payload(secret, 1000, body)
        ok, err = verify_webhook_signature(secret, body, "1000", sig, now_sec=1100)
        self.assertTrue(ok)
        self.assertIsNone(err)

        ok_stale, err_stale = verify_webhook_signature(secret, body, "1000", sig, now_sec=1500)
        self.assertFalse(ok_stale)
        self.assertEqual(err_stale, "REPLAY_WINDOW_EXCEEDED")

        ok_tamper, err_tamper = verify_webhook_signature(secret, b'{"erp_ref":"ORD-2"}', "1000", sig, now_sec=1100)
        self.assertFalse(ok_tamper)
        self.assertEqual(err_tamper, "SIGNATURE_MISMATCH")

    def test_coerces_currency_enforces_monotonic_upsert_and_scrubs_dlq_pii(self):
        store = {"orders": {}, "dlq": []}

        # 1. Dirty valid currency string -> coerced with warning
        body_ok = json.dumps({
            "erp_ref": "ORD-99",
            "gross_usd": " $1,240.50 ",
            "fulfillment_state": "",
            "modified_epoch_ms": 2000,
        }).encode("utf-8")
        res1 = ingest_webhook_event(body_ok, self.cfg, store, now_ms=2000)
        self.assertEqual(res1["bucket"], "upserted")
        self.assertEqual(store["orders"]["acme_logistics::ORD-99"]["amount"], 1240.50)
        self.assertEqual(store["orders"]["acme_logistics::ORD-99"]["status"], "pending")

        # 2. Out-of-order stale retry -> skipped cleanly without overwriting newer state
        body_stale = json.dumps({
            "erp_ref": "ORD-99",
            "gross_usd": 10.0,
            "fulfillment_state": "cancelled",
            "modified_epoch_ms": 1500,
        }).encode("utf-8")
        res2 = ingest_webhook_event(body_stale, self.cfg, store, now_ms=2100)
        self.assertEqual(res2["bucket"], "skipped_stale")
        self.assertEqual(store["orders"]["acme_logistics::ORD-99"]["amount"], 1240.50)

        # 3. Negative amount with email/SSN PII -> quarantined into DLQ with PII scrubbed & HTTP 202
        body_bad = json.dumps({
            "erp_ref": "ORD-100",
            "gross_usd": -25,
            "note": "Contact ceo@acme.io SSN 123-45-6789",
        }).encode("utf-8")
        res3 = ingest_webhook_event(body_bad, self.cfg, store, now_ms=2200)
        self.assertEqual(res3["http_status"], 202)
        self.assertEqual(res3["bucket"], "dlq")
        dlq_summary = store["dlq"][0]["safe_summary"]
        self.assertNotIn("ceo@acme.io", dlq_summary)
        self.assertNotIn("123-45-6789", dlq_summary)
        self.assertIn("[REDACTED_EMAIL]", dlq_summary)
        self.assertIn("[REDACTED_SSN]", dlq_summary)


if __name__ == "__main__":
    unittest.main()
