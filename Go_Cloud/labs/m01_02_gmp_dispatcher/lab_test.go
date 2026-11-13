package dispatcher

import (
	"sync/atomic"
	"testing"
	"time"
)

func TestDispatcher_BoundedConcurrency(t *testing.T) {
	const limit = 4
	d := NewDispatcher(limit)

	var inFlight atomic.Int32
	var maxObserved atomic.Int32
	var completed atomic.Int32

	for range 40 {
		d.Submit(func() {
			cur := inFlight.Add(1)
			for {
				prev := maxObserved.Load()
				if cur <= prev || maxObserved.CompareAndSwap(prev, cur) {
					break
				}
			}
			time.Sleep(2 * time.Millisecond)
			inFlight.Add(-1)
			completed.Add(1)
		})
	}
	d.Wait()

	if completed.Load() != 40 {
		t.Fatalf("expected 40 completed tasks, got %d", completed.Load())
	}
	if maxObserved.Load() > limit {
		t.Fatalf("concurrency limit exceeded: maxObserved=%d > limit=%d", maxObserved.Load(), limit)
	}
}
