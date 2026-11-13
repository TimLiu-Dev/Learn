package testingsynctest

import (
	"context"
	"sync"
	"time"
)

// SafeBag is a concurrency-safe counter map tested with `go test -race`.
type SafeBag struct {
	mu     sync.Mutex
	counts map[string]int
}

func NewSafeBag() *SafeBag {
	return &SafeBag{counts: make(map[string]int)}
}

// Add increments key safely across concurrent goroutines.
func (b *SafeBag) Add(key string) {
	// TODO: Protect map mutation with b.mu.Lock()/Unlock().
	b.counts[key]++
}

func (b *SafeBag) Get(key string) int {
	b.mu.Lock()
	defer b.mu.Unlock()
	return b.counts[key]
}

// RetryAfterDelay waits for delay or returns early if ctx cancels.
// Tested instantaneously with Go 1.25's testing/synctest virtual clock!
func RetryAfterDelay(ctx context.Context, delay time.Duration) error {
	// TODO: Wait using time.NewTimer(delay) (always Stop timer on exit) and select on ctx.Done().
	_ = time.NewTimer
	return nil
}
