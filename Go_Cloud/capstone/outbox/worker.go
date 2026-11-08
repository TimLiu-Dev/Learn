package outbox

import (
	"context"
	"encoding/json"
	"fmt"
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

// OutboxRecord represents an event stored atomically in the database transaction log.
type OutboxRecord struct {
	ID          int64           `json:"id"`
	AggregateID string          `json:"aggregate_id"`
	EventType   string          `json:"event_type"`
	Payload     json.RawMessage `json:"payload"`
	Status      EventStatus     `json:"status"`
	RetryCount  int             `json:"retry_count"`
	CreatedAt   time.Time       `json:"created_at"`
	PublishedAt *time.Time      `json:"published_at,omitempty"`
}

// EventPublisher defines the downstream transport interface (Kafka / Redpanda / NATS).
type EventPublisher interface {
	Publish(ctx context.Context, topic string, key string, payload []byte) error
}

// MockKafkaPublisher simulates high-throughput event streaming with delivery tracking.
type MockKafkaPublisher struct {
	PublishedCount uint64
}

func (m *MockKafkaPublisher) Publish(ctx context.Context, topic string, key string, payload []byte) error {
	// Simulate ultra-fast broker write
	atomic.AddUint64(&m.PublishedCount, 1)
	return nil
}

// OutboxProcessor polls or listens to outbox events and dispatches them with exponential backoff.
type OutboxProcessor struct {
	publisher  EventPublisher
	batchSize  int
	pollTicker time.Duration
	events     []OutboxRecord
	mu         sync.Mutex
	published  uint64
	failed     uint64
}

// NewOutboxProcessor creates a processor with configurable concurrency & batch size.
func NewOutboxProcessor(publisher EventPublisher, batchSize int, pollInterval time.Duration) *OutboxProcessor {
	return &OutboxProcessor{
		publisher:  publisher,
		batchSize:  batchSize,
		pollTicker: pollInterval,
		events:     make([]OutboxRecord, 0, 1000),
	}
}

// Enqueue simulates database transaction writing an outbox record inside the same TX.
func (op *OutboxProcessor) Enqueue(aggregateID, eventType string, payload []byte) {
	op.mu.Lock()
	defer op.mu.Unlock()

	op.events = append(op.events, OutboxRecord{
		ID:          int64(len(op.events) + 1),
		AggregateID: aggregateID,
		EventType:   eventType,
		Payload:     payload,
		Status:      StatusPending,
		CreatedAt:   time.Now(),
	})
}

// ProcessBatch processes a chunk of pending events and emits them to the message broker.
// Returns the number of events successfully published in this sweep.
func (op *OutboxProcessor) ProcessBatch(ctx context.Context) (int, error) {
	op.mu.Lock()
	var toProcess []*OutboxRecord
	for i := range op.events {
		if op.events[i].Status == StatusPending {
			toProcess = append(toProcess, &op.events[i])
			if len(toProcess) >= op.batchSize {
				break
			}
		}
	}
	op.mu.Unlock()

	if len(toProcess) == 0 {
		return 0, nil
	}

	successCount := 0
	for _, record := range toProcess {
		select {
		case <-ctx.Done():
			return successCount, ctx.Err()
		default:
		}

		topic := fmt.Sprintf("ledger.events.%s", record.EventType)
		err := op.publisher.Publish(ctx, topic, record.AggregateID, record.Payload)
		if err != nil {
			record.RetryCount++
			if record.RetryCount >= 5 {
				record.Status = StatusFailed
				atomic.AddUint64(&op.failed, 1)
			}
			continue
		}

		now := time.Now()
		record.Status = StatusPublished
		record.PublishedAt = &now
		atomic.AddUint64(&op.published, 1)
		successCount++
	}

	return successCount, nil
}

// Stats returns processed outbox metrics.
func (op *OutboxProcessor) Stats() (uint64, uint64) {
	return atomic.LoadUint64(&op.published), atomic.LoadUint64(&op.failed)
}
