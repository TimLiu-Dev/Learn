package logging

import "log/slog"

// RedactAttr scrubs sensitive keys case-insensitively (even inside slog.Group contexts).
// Sensitive keys: "authorization", "password", "secret_token", "api_key", "token".
func RedactAttr(groups []string, a slog.Attr) slog.Attr {
	// TODO: Check strings.ToLower(a.Key) and return slog.String(a.Key, "[REDACTED]") if matched.
	return a
}
