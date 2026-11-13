package sse

import (
	"context"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"
)

func TestStreamHandler(t *testing.T) {
	tokens := make(chan string, 2)
	tokens <- "hello"
	tokens <- "world"
	close(tokens)

	rec := httptest.NewRecorder()
	req := httptest.NewRequest(http.MethodGet, "/stream", nil)
	StreamHandler(rec, req, tokens)

	if ct := rec.Header().Get("Content-Type"); !strings.HasPrefix(ct, "text/event-stream") {
		t.Fatalf("expected text/event-stream Content-Type, got %q", ct)
	}
	if !rec.Flushed {
		t.Fatal("expected ResponseWriter to be flushed via http.NewResponseController(w).Flush()")
	}
	got := rec.Body.String()
	for _, want := range []string{"data: hello\n\n", "data: world\n\n", "data: [DONE]\n\n"} {
		if !strings.Contains(got, want) {
			t.Fatalf("SSE stream missing %q in %q", want, got)
		}
	}
}

func TestStreamHandler_ClientDisconnect(t *testing.T) {
	ctx, cancel := context.WithCancel(context.Background())
	req := httptest.NewRequest(http.MethodGet, "/stream", nil).WithContext(ctx)
	rec := httptest.NewRecorder()
	unclosed := make(chan string)

	done := make(chan struct{})
	go func() {
		StreamHandler(rec, req, unclosed)
		close(done)
	}()

	cancel()
	select {
	case <-done:
	case <-time.After(time.Second):
		t.Fatal("StreamHandler did not abort promptly when client disconnected")
	}
}
