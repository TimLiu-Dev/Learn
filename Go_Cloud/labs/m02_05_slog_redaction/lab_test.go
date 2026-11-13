package logging

import (
	"bytes"
	"encoding/json"
	"log/slog"
	"testing"
)

func TestRedactAttr_CaseInsensitiveAndGroups(t *testing.T) {
	var buf bytes.Buffer
	logger := slog.New(slog.NewJSONHandler(&buf, &slog.HandlerOptions{
		ReplaceAttr: RedactAttr,
	}))

	logger.Info("auth attempt",
		slog.String("user_id", "u-123"),
		slog.String("AUTHORIZATION", "Bearer secret-jwt"),
		slog.Group("headers",
			slog.String("Api_Key", "sk-live-999"),
			slog.String("x_request_id", "req-1"),
		),
	)

	var decoded map[string]any
	if err := json.Unmarshal(buf.Bytes(), &decoded); err != nil {
		t.Fatalf("invalid JSON log: %v", err)
	}
	if decoded["user_id"] != "u-123" {
		t.Fatalf("expected user_id u-123, got %v", decoded["user_id"])
	}
	if decoded["AUTHORIZATION"] != "[REDACTED]" {
		t.Fatalf("AUTHORIZATION leaked: %v", decoded["AUTHORIZATION"])
	}
	hdrs := decoded["headers"].(map[string]any)
	if hdrs["Api_Key"] != "[REDACTED]" {
		t.Fatalf("grouped Api_Key leaked: %v", hdrs["Api_Key"])
	}
	if hdrs["x_request_id"] != "req-1" {
		t.Fatalf("non-sensitive grouped field altered: %v", hdrs["x_request_id"])
	}
}
