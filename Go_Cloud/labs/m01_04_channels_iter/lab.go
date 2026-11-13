package pipeline

import (
	"context"
	"iter"
	"sync"
)

// FanOutProcess distributes jobs across workerCount workers and closes out once drained or cancelled.
func FanOutProcess(ctx context.Context, jobs <-chan int, workerCount int, transform func(int) int) <-chan int {
	out := make(chan int)
	var wg sync.WaitGroup
	// TODO: Spawn workerCount workers using select on ctx.Done() and jobs, then close(out) after wg.Wait().
	wg.Wait()
	return out
}

// FilterSeq returns a Go 1.23+ push iterator yielding elements satisfying keep with zero channel allocations.
func FilterSeq[T any](items []T, keep func(T) bool) iter.Seq[T] {
	return func(yield func(T) bool) {
		// TODO: Iterate items; if keep(item) and !yield(item), return immediately.
	}
}
