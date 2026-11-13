package outbox

import (
	"context"
	"errors"
	"sync"
	"sync/atomic"
	"testing"
	"time"
)

type flakyPublisher struct {
	failUntil int32
	calls     atomic.Int32
}

func (f *flakyPublisher) Publish(ctx context.Context, topic, key string, payload []byte) error {
	if f.calls.Add(1) <= f.failUntil {
		return errors.New("broker unavailable")
	}
	return nil
}

func TestOutboxProcessor_ConcurrentEnqueueAndProcess(t *testing.T) {
	pub := &MockKafkaPublisher{}
	proc := NewOutboxProcessor(pub, 16, 10*time.Millisecond)

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	var wg sync.WaitGroup
	wg.Add(1)
	go func() {
		defer wg.Done()
		_ = proc.Run(ctx)
	}()

	for i := range 200 {
		proc.Enqueue("acc-1", "transfer", []byte(`{"amount":100}`))
		if i%25 == 0 {
			time.Sleep(time.Millisecond)
		}
	}

	deadline := time.Now().Add(2 * time.Second)
	for time.Now().Before(deadline) {
		published, _ := proc.Stats()
		if published == 200 {
			break
		}
		time.Sleep(10 * time.Millisecond)
	}
	cancel()
	wg.Wait()

	published, failed := proc.Stats()
	if published != 200 || failed != 0 {
		t.Fatalf("expected 200 published / 0 failed, got %d / %d", published, failed)
	}
}

func TestOutboxProcessor_RetryAndDeadLetter(t *testing.T) {
	pub := &flakyPublisher{failUntil: 100}
	proc := NewOutboxProcessor(pub, 10, time.Millisecond)
	proc.baseDelay = time.Millisecond // fast backoff for unit test
	proc.Enqueue("acc-2", "transfer", []byte(`{}`))

	ctx := context.Background()
	for range maxRetries {
		proc.mu.Lock()
		proc.events[0].NextAttempt = time.Now().Add(-time.Millisecond)
		proc.mu.Unlock()
		_, _ = proc.ProcessBatch(ctx)
	}

	published, failed := proc.Stats()
	if published != 0 || failed != 1 {
		t.Fatalf("expected 0 published / 1 failed after maxRetries, got %d / %d", published, failed)
	}
}
