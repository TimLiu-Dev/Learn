package bufferpool

import (
	"runtime"
	"testing"
)

func TestBufferPool_ZeroAllocReuse(t *testing.T) {
	bp := NewBufferPool()
	first := bp.Get()
	if first == nil || cap(*first) != DefaultCapacity {
		t.Fatalf("expected non-nil buffer with cap %d", DefaultCapacity)
	}
	*first = append(*first, []byte("hello")...)
	bp.Put(first)

	allocs := testing.AllocsPerRun(50, func() {
		buf := bp.Get()
		if len(*buf) != 0 {
			t.Fatalf("pooled buffer was not reset to len 0: %d", len(*buf))
		}
		*buf = append(*buf, 'x')
		bp.Put(buf)
	})
	if allocs != 0 {
		t.Fatalf("expected 0 allocs/run after warmup, got %.1f", allocs)
	}
}

func TestWeakCache_AllowsGCReclaim(t *testing.T) {
	type payload struct{ data [1024]byte }
	cache := NewWeakCache[string, payload]()

	func() {
		v := &payload{}
		cache.Set("ephemeral", v)
		if got, ok := cache.Get("ephemeral"); !ok || got == nil {
			t.Fatal("expected cached value while strong reference is alive")
		}
	}()

	// Trigger GC until weak pointer clears (or verify basic Set/Get).
	for range 5 {
		runtime.GC()
	}
	if got, ok := cache.Get("ephemeral"); ok && got != nil {
		t.Fatal("expected weak entry to be reclaimed after strong reference dropped and GC ran")
	}
}
