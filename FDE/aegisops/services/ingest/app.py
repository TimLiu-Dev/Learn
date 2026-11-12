"""FastAPI entrypoint wiring HMAC verification and config-driven ingestion."""

import json
import os
import time
from pathlib import Path

from fastapi import FastAPI, HTTPException, Request, Response

from .hmac_guard import verify_webhook_signature
from .triage import ingest_webhook_event

app = FastAPI(title="AegisOps Ingestion Service", version="1.0.0")
CONFIG_DIR = Path(__file__).resolve().parents[2] / "configs" / "tenants"
IN_MEMORY_STORE: dict = {"orders": {}, "dlq": []}


def load_tenant_config(tenant_id: str) -> dict:
    cfg_path = CONFIG_DIR / f"{tenant_id}.json"
    if not cfg_path.exists():
        raise HTTPException(status_code=404, detail="Unknown tenant")
    return json.loads(cfg_path.read_text(encoding="utf-8"))


@app.get("/healthz")
async def healthz():
    return {"status": "ok"}


@app.post("/v1/webhooks/{tenant_id}")
async def receive_webhook(tenant_id: str, request: Request, response: Response):
    cfg = load_tenant_config(tenant_id)
    secret = os.environ.get(cfg["webhook_secret_env"], "dev_secret_change_me").encode("utf-8")
    raw_body = await request.body()

    ok, err = verify_webhook_signature(
        secret=secret,
        raw_body=raw_body,
        timestamp_header=request.headers.get("x-webhook-timestamp", ""),
        signature_header=request.headers.get("x-webhook-signature", ""),
    )
    if not ok:
        raise HTTPException(status_code=401, detail=err)

    result = ingest_webhook_event(raw_body, cfg, IN_MEMORY_STORE, now_ms=int(time.time() * 1000))
    response.status_code = result["http_status"]
    return result
