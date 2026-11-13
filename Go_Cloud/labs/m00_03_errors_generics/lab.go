package errorsgenerics

import (
	"cmp"
	"errors"
	"fmt"
	"time"
)

var ErrRecordNotFound = errors.New("record not found")

// RateLimitError is a typed domain error carrying a retry delay.
type RateLimitError struct {
	RetryAfter time.Duration
}

func (e *RateLimitError) Error() string {
	return fmt.Sprintf("rate limited; retry after %s", e.RetryAfter)
}

// WrapNotFound wraps ErrRecordNotFound with entity context using %w.
func WrapNotFound(entity string, id string) error {
	// TODO: Return formatted error wrapping ErrRecordNotFound with %w.
	return nil
}

// ExtractRetryAfter extracts RetryAfter from an error chain using errors.AsType (Go 1.26+).
func ExtractRetryAfter(err error) (time.Duration, bool) {
	// TODO: Use rl, ok := errors.AsType[*RateLimitError](err)
	return 0, false
}

// MaxSlice returns the maximum element in items using Go generics with cmp.Ordered.
// Returns (zero, false) if items is empty.
func MaxSlice[T cmp.Ordered](items []T) (T, bool) {
	// TODO: Find and return maximum value.
	var zero T
	return zero, false
}
