package outbox

import (
	"context"
	"encoding/json"
	"fmt"
	"math/rand/v2"
	"sync"
	"sync/atomic"
	"time"
)

// EventStatus tracks outbox event lifecycle.
type EventStatus int8

const (
	StatusPending EventStatus = iota
	StatusPublished
	StatusFailed
)

const maxRetries = 5

// OutboxRecord represents an event stored atomically in the database transaction log.
type OutboxRecord struct {
	ID          int64           `json:"id"`
	AggregateID string          `json:"aggregate_id"`
	EventType   string          `json:"event_type"`
	Payload     json.RawMessage `json:"payload"`
	Status      EventStatus     `json:"status"`
	RetryCount  int             `json:"retry_count"`
	NextAttempt time.Time       `json:"next_attempt"`
	CreatedAt   time.Time       `json:"created_at"`
	PublishedAt *time.Time      `json:"published_at,omitempty"`
}

// EventPublisher defines the downstream transport interface (Kafka / Redpanda / NATS).
type EventPublisher interface {
	Publish(ctx context.Context, topic string, key string, payload []byte) error
}

// MockKafkaPublisher simulates high-throughput event streaming with delivery tracking.
type MockKafkaPublisher struct {
	PublishedCount atomic.Uint64
}

func (m *MockKafkaPublisher) Publish(ctx context.Context, topic string, key string, payload []byte) error {
	m.PublishedCount.Add(1)
	return nil
}

// OutboxProcessor polls outbox events and dispatches them with exponential backoff + jitter.
type OutboxProcessor struct {
	publisher  EventPublisher
	batchSize  int
	pollTicker time.Duration
	baseDelay  time.Duration
	events     []OutboxRecord
	mu         sync.Mutex
	published  atomic.Uint64
	failed     atomic.Uint64
}

// NewOutboxProcessor creates a processor with configurable batch size and poll interval.
func NewOutboxProcessor(publisher EventPublisher, batchSize int, pollInterval time.Duration) *OutboxProcessor {
	if batchSize <= 0 {
		batchSize = 100
	}
	if pollInterval <= 0 {
		pollInterval = 100 * time.Millisecond
	}
	return &OutboxProcessor{
		publisher:  publisher,
		batchSize:  batchSize,
		pollTicker: pollInterval,
		baseDelay:  50 * time.Millisecond,
		events:     make([]OutboxRecord, 0, 1000),
	}
}

// Enqueue simulates a database transaction writing an outbox record inside the same TX.
func (op *OutboxProcessor) Enqueue(aggregateID, eventType string, payload []byte) {
	op.mu.Lock()
	defer op.mu.Unlock()

	now := time.Now()
	payloadCopy := append([]byte(nil), payload...)
	op.events = append(op.events, OutboxRecord{
		ID:          int64(len(op.events) + 1),
		AggregateID: aggregateID,
		EventType:   eventType,
		Payload:     payloadCopy,
		Status:      StatusPending,
		NextAttempt: now,
		CreatedAt:   now,
	})
}

type batchCandidate struct {
	index  int
	record OutboxRecord
}

// ProcessBatch processes due pending events without holding the mutex during network I/O
// and safely writes results back by index so concurrent Enqueue calls never race.
func (op *OutboxProcessor) ProcessBatch(ctx context.Context) (int, error) {
	now := time.Now()

	op.mu.Lock()
	candidates := make([]batchCandidate, 0, op.batchSize)
	for i := range op.events {
		ev := op.events[i]
		if ev.Status == StatusPending && !ev.NextAttempt.After(now) {
			candidates = append(candidates, batchCandidate{index: i, record: ev})
			if len(candidates) >= op.batchSize {
				break
			}
		}
	}
	op.mu.Unlock()

	if len(candidates) == 0 {
		return 0, nil
	}

	successCount := 0
	for _, item := range candidates {
		if err := ctx.Err(); err != nil {
			return successCount, err
		}

		topic := fmt.Sprintf("ledger.events.%s", item.record.EventType)
		err := op.publisher.Publish(ctx, topic, item.record.AggregateID, item.record.Payload)

		op.mu.Lock()
		rec := &op.events[item.index]
		if err != nil {
			rec.RetryCount++
			if rec.RetryCount >= maxRetries {
				rec.Status = StatusFailed
				op.failed.Add(1)
			} else {
				rec.NextAttempt = time.Now().Add(backoffWithJitter(op.baseDelay, rec.RetryCount))
			}
			op.mu.Unlock()
			continue
		}

		pubTime := time.Now()
		rec.Status = StatusPublished
		rec.PublishedAt = &pubTime
		op.mu.Unlock()

		op.published.Add(1)
		successCount++
	}

	return successCount, nil
}

// Run polls and dispatches outbox batches until ctx is cancelled.
func (op *OutboxProcessor) Run(ctx context.Context) error {
	ticker := time.NewTicker(op.pollTicker)
	defer ticker.Stop()

	for {
		if _, err := op.ProcessBatch(ctx); err != nil {
			return err
		}
		select {
		case <-ctx.Done():
			return ctx.Err()
		case <-ticker.C:
		}
	}
}

func backoffWithJitter(base time.Duration, attempt int) time.Duration {
	delay := base << min(attempt, 6)
	if delay <= 0 {
		return base
	}
	// Full jitter between [delay/2, delay)
	half := delay / 2
	return half + time.Duration(rand.Int64N(int64(half)+1))
}

// Stats returns processed outbox metrics.
func (op *OutboxProcessor) Stats() (uint64, uint64) {
	return op.published.Load(), op.failed.Load()
}
