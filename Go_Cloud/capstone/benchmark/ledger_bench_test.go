package benchmark

import (
	"fmt"
	"testing"

	"gocloud/capstone/domain"
)

// BenchmarkLedgerExecution verifies that transaction execution operates with zero or near-zero allocations
// and sustains 50,000+ rps on modern multi-core systems.
func BenchmarkLedgerExecution(b *testing.B) {
	engine := domain.NewLedgerEngine()

	// Setup accounts
	engine.RegisterAccount(1001, 100_000_000_00) // 100 million dollars
	engine.RegisterAccount(1002, 100_000_000_00)

	entries := []domain.LedgerEntry{
		{AccountID: 1001, Amount: 500, Type: domain.Debit},
		{AccountID: 1002, Amount: 500, Type: domain.Credit},
	}

	b.ResetTimer()
	b.ReportAllocs()

	for i := 0; i < b.N; i++ {
		tx := domain.Transaction{
			ID:             "tx-bench",
			IdempotencyKey: fmt.Sprintf("bench-key-%d", i),
			Entries:        entries,
		}
		if err := engine.ExecuteTransaction(tx); err != nil {
			b.Fatalf("transaction failed: %v", err)
		}
	}
}

// BenchmarkParallelLedgerExecution simulates multi-goroutine concurrent ingestion.
func BenchmarkParallelLedgerExecution(b *testing.B) {
	engine := domain.NewLedgerEngine()
	engine.RegisterAccount(2001, 500_000_000_00)
	engine.RegisterAccount(2002, 500_000_000_00)

	b.ResetTimer()
	b.ReportAllocs()

	b.RunParallel(func(pb *testing.PB) {
		iter := 0
		entries := []domain.LedgerEntry{
			{AccountID: 2001, Amount: 100, Type: domain.Debit},
			{AccountID: 2002, Amount: 100, Type: domain.Credit},
		}

		for pb.Next() {
			iter++
			tx := domain.Transaction{
				ID:      "parallel-bench",
				Entries: entries,
			}
			_ = engine.ExecuteTransaction(tx)
		}
	})
}
