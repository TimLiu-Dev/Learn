package interceptors

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

type staticLimiter struct{ allow bool }

func (s staticLimiter) Allow(string) bool { return s.allow }

func TestRateLimitingMiddleware(t *testing.T) {
	called := false
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		called = true
		w.WriteHeader(http.StatusOK)
	})

	blockedHandler := RateLimitingMiddleware(staticLimiter{allow: false}, next)
	rec := httptest.NewRecorder()
	blockedHandler.ServeHTTP(rec, httptest.NewRequest(http.MethodPost, "/ledger.v1.LedgerService/Transfer", nil))

	if rec.Code != http.StatusTooManyRequests {
		t.Fatalf("expected 429 Too Many Requests, got %d", rec.Code)
	}
	if !strings.Contains(rec.Body.String(), "resource_exhausted") {
		t.Fatalf("expected Connect-compatible resource_exhausted JSON body, got %q", rec.Body.String())
	}
	if called {
		t.Fatal("next handler must not be called when rate-limited")
	}

	okHandler := RateLimitingMiddleware(staticLimiter{allow: true}, next)
	rec2 := httptest.NewRecorder()
	okHandler.ServeHTTP(rec2, httptest.NewRequest(http.MethodPost, "/ledger.v1.LedgerService/Transfer", nil))
	if !called || rec2.Code != http.StatusOK {
		t.Fatalf("expected allowed request to invoke next handler with 200; called=%v code=%d", called, rec2.Code)
	}
}
