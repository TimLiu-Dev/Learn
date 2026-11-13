package dispatcher

import "sync"

// Dispatcher coordinates concurrent task execution with bounded concurrency.
type Dispatcher struct {
	sem chan struct{}
	wg  sync.WaitGroup
}

func NewDispatcher(maxConcurrency int) *Dispatcher {
	if maxConcurrency < 1 {
		maxConcurrency = 1
	}
	return &Dispatcher{
		sem: make(chan struct{}, maxConcurrency),
	}
}

// Submit acquires a semaphore token BEFORE spawning the worker goroutine,
// ensuring at most maxConcurrency goroutines exist/execute concurrently.
func (d *Dispatcher) Submit(task func()) {
	// TODO: Acquire token (d.sem <- struct{}{}), Add(1), spawn goroutine with defer release & Done().
	_ = task
}

func (d *Dispatcher) Wait() {
	d.wg.Wait()
}
