package testingsynctest

import (
	"context"
	"errors"
	"sync"
	"testing"
	"testing/synctest"
	"time"
)

func TestSafeBag_ConcurrentRace(t *testing.T) {
	bag := NewSafeBag()
	var wg sync.WaitGroup
	for range 50 {
		wg.Add(1)
		go func() {
			defer wg.Done()
			for range 100 {
				bag.Add("requests")
			}
		}()
	}
	wg.Wait()

	if got := bag.Get("requests"); got != 5000 {
		t.Fatalf("expected 5000, got %d", got)
	}
}

func TestRetryAfterDelay_SynctestVirtualClock(t *testing.T) {
	synctest.Test(t, func(t *testing.T) {
		ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
		defer cancel()

		var err error
		done := make(chan struct{})
		go func() {
			err = RetryAfterDelay(ctx, 10*time.Second)
			close(done)
		}()

		// Advance virtualized bubble clock past the 2s context deadline instantly (0ms wall time!).
		time.Sleep(3 * time.Second)
		synctest.Wait()
		<-done

		if !errors.Is(err, context.DeadlineExceeded) {
			t.Fatalf("expected context.DeadlineExceeded, got %v", err)
		}
	})
}

func TestRetryAfterDelay_CompletesBeforeCancel(t *testing.T) {
	synctest.Test(t, func(t *testing.T) {
		ctx := context.Background()
		var err error
		done := make(chan struct{})
		go func() {
			err = RetryAfterDelay(ctx, 5*time.Second)
			close(done)
		}()

		time.Sleep(5 * time.Second)
		synctest.Wait()
		<-done

		if err != nil {
			t.Fatalf("expected nil error after delay, got %v", err)
		}
	})
}
