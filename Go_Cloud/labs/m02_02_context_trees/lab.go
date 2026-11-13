package fetcher

import (
	"context"
	"time"
)

type WorkerFunc func(ctx context.Context) (string, error)

// FetchWithTimeout runs worker under a derived timeout context without leaking goroutines or timers.
func FetchWithTimeout(parentCtx context.Context, timeout time.Duration, worker WorkerFunc) (string, error) {
	// TODO: Derive ctx, cancel := context.WithTimeout(parentCtx, timeout); defer cancel().
	// Send result on buffered channel (cap 1) and select between <-ctx.Done() and <-resCh.
	return "", nil
}
