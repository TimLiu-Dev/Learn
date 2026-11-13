package tracker

import "sync/atomic"

// LatencyTracker tracks total requests and peak latency using lock-free atomics.
type LatencyTracker struct {
	totalRequests atomic.Uint64
	maxLatencyNs  atomic.Uint64
}

func NewLatencyTracker() *LatencyTracker {
	return &LatencyTracker{}
}

func (t *LatencyTracker) Increment() {
	// TODO: Atomically increment t.totalRequests by 1.
}

func (t *LatencyTracker) RecordLatency(latencyNs uint64) {
	// TODO: Implement lock-free CompareAndSwap loop updating maxLatencyNs only if latencyNs > current.
}

func (t *LatencyTracker) Stats() (uint64, uint64) {
	return t.totalRequests.Load(), t.maxLatencyNs.Load()
}
