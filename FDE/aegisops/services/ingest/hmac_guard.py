"""Cryptographic HMAC-SHA256 webhook verification with replay-window enforcement."""

from __future__ import annotations

import hashlib
import hmac
import time


def verify_webhook_signature(
    secret: bytes,
    raw_body: bytes,
    timestamp_header: str,
    signature_header: str,
    now_sec: int | None = None,
    max_skew_sec: int = 300,
) -> tuple[bool, str | None]:
    """Verify raw-body HMAC signature and timestamp window using constant-time comparison."""
    if not timestamp_header or not timestamp_header.strip().lstrip("-").isdigit():
        return False, "INVALID_TIMESTAMP"

    ts = int(timestamp_header.strip())
    current = int(time.time()) if now_sec is None else int(now_sec)
    if abs(current - ts) > max_skew_sec:
        return False, "REPLAY_WINDOW_EXCEEDED"

    signed_payload = f"{ts}.".encode("utf-8") + raw_body
    expected = "v1=" + hmac.new(secret, signed_payload, hashlib.sha256).hexdigest()
    if not signature_header or not hmac.compare_digest(expected, signature_header.strip()):
        return False, "SIGNATURE_MISMATCH"

    return True, None
