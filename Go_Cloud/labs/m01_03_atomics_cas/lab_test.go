package tracker

import (
	"sync"
	"testing"
)

func TestLatencyTracker_ConcurrentCAS(t *testing.T) {
	lt := NewLatencyTracker()
	var wg sync.WaitGroup

	for g := range 64 {
		wg.Add(1)
		go func(id int) {
			defer wg.Done()
			for i := range 200 {
				lt.Increment()
				lt.RecordLatency(uint64(id*100 + i))
			}
		}(g)
	}
	wg.Wait()

	total, maxNs := lt.Stats()
	if total != 64*200 {
		t.Fatalf("expected %d total requests, got %d", 64*200, total)
	}
	wantMax := uint64(63*100 + 199)
	if maxNs != wantMax {
		t.Fatalf("expected maxLatencyNs=%d, got %d", wantMax, maxNs)
	}
}
