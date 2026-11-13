package nethttpbridge

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestNewOrderMux(t *testing.T) {
	mux := NewOrderMux()

	t.Run("GET order by path param", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodGet, "/orders/ord-77", nil)
		rec := httptest.NewRecorder()
		mux.ServeHTTP(rec, req)

		if rec.Code != http.StatusOK {
			t.Fatalf("expected 200, got %d", rec.Code)
		}
		var resp OrderResponse
		if err := json.NewDecoder(rec.Body).Decode(&resp); err != nil {
			t.Fatalf("invalid JSON: %v", err)
		}
		if resp.ID != "ord-77" {
			t.Fatalf("expected id ord-77, got %q", resp.ID)
		}
	})

	t.Run("Method not allowed returns 405", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodDelete, "/orders/ord-77", nil)
		rec := httptest.NewRecorder()
		mux.ServeHTTP(rec, req)

		if rec.Code != http.StatusMethodNotAllowed {
			t.Fatalf("expected 405, got %d", rec.Code)
		}
	})

	t.Run("POST valid order returns 201", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodPost, "/orders", strings.NewReader(`{"id":"ord-99","total_cents":1500}`))
		rec := httptest.NewRecorder()
		mux.ServeHTTP(rec, req)

		if rec.Code != http.StatusCreated {
			t.Fatalf("expected 201, got %d", rec.Code)
		}
	})

	t.Run("POST invalid JSON returns 400", func(t *testing.T) {
		req := httptest.NewRequest(http.MethodPost, "/orders", strings.NewReader(`{bad`))
		rec := httptest.NewRecorder()
		mux.ServeHTTP(rec, req)

		if rec.Code != http.StatusBadRequest {
			t.Fatalf("expected 400, got %d", rec.Code)
		}
	})
}
