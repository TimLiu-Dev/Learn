package nethttpbridge

import (
	"encoding/json"
	"net/http"
)

// OrderResponse uses Go 1.24's omitzero tag for optional timestamps/amounts.
type OrderResponse struct {
	ID    string `json:"id"`
	Total int64  `json:"total_cents,omitzero"`
}

// NewOrderMux constructs a Go 1.22+ ServeMux supporting method + path parameters:
// - GET /orders/{id} -> 200 JSON {"id":"<id>","total_cents":4200}
// - POST /orders     -> decodes OrderResponse JSON (400 if invalid/empty ID), returns 201 JSON
func NewOrderMux() *http.ServeMux {
	mux := http.NewServeMux()
	// TODO: Register "GET /orders/{id}" using r.PathValue("id") and "POST /orders".
	_ = json.NewEncoder
	return mux
}
