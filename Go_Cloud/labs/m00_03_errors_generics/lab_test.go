package errorsgenerics

import (
	"errors"
	"fmt"
	"testing"
	"time"
)

func TestWrapNotFound(t *testing.T) {
	err := WrapNotFound("account", "acc-42")
	if !errors.Is(err, ErrRecordNotFound) {
		t.Fatalf("errors.Is(err, ErrRecordNotFound) = false; got %v", err)
	}
}

func TestExtractRetryAfter(t *testing.T) {
	wrapped := fmt.Errorf("gateway call: %w", &RateLimitError{RetryAfter: 5 * time.Second})
	d, ok := ExtractRetryAfter(wrapped)
	if !ok || d != 5*time.Second {
		t.Fatalf("ExtractRetryAfter() = (%v, %v), want (5s, true)", d, ok)
	}
	if _, ok := ExtractRetryAfter(errors.New("other")); ok {
		t.Fatal("expected false for unrelated error")
	}
}

func TestMaxSlice(t *testing.T) {
	if _, ok := MaxSlice([]int(nil)); ok {
		t.Fatal("expected false on empty slice")
	}
	got, ok := MaxSlice([]int64{12, 99, 45})
	if !ok || got != 99 {
		t.Fatalf("MaxSlice ints = (%d, %v), want (99, true)", got, ok)
	}
	gotStr, ok := MaxSlice([]string{"alpha", "zeta", "beta"})
	if !ok || gotStr != "zeta" {
		t.Fatalf("MaxSlice strings = (%q, %v), want (zeta, true)", gotStr, ok)
	}
}
