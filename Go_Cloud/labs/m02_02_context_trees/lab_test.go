package fetcher

import (
	"context"
	"errors"
	"runtime"
	"testing"
	"time"
)

func TestFetchWithTimeout_SuccessAndDeadline(t *testing.T) {
	val, err := FetchWithTimeout(context.Background(), 50*time.Millisecond, func(ctx context.Context) (string, error) {
		return "ok", nil
	})
	if err != nil || val != "ok" {
		t.Fatalf("expected (ok, nil), got (%q, %v)", val, err)
	}

	before := runtime.NumGoroutine()
	unblock := make(chan struct{})
	defer close(unblock)

	_, err = FetchWithTimeout(context.Background(), 5*time.Millisecond, func(ctx context.Context) (string, error) {
		<-unblock
		return "late", nil
	})
	if !errors.Is(err, context.DeadlineExceeded) {
		t.Fatalf("expected context.DeadlineExceeded, got %v", err)
	}
	_ = before
}
