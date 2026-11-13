package benchmark

import (
	"fmt"
	"testing"

	"gocloud/capstone/domain"
)

// BenchmarkValidateDoubleEntry verifies the hot-path invariant check runs with zero heap allocations.
func BenchmarkValidateDoubleEntry(b *testing.B) {
	entries := []domain.LedgerEntry{
		{AccountID: 1001, Amount: 500, Type: domain.Debit},
		{AccountID: 1002, Amount: 500, Type: domain.Credit},
	}
	b.ReportAllocs()
	for b.Loop() {
		if err := domain.ValidateDoubleEntry(entries); err != nil {
			b.Fatal(err)
		}
	}
}

// BenchmarkLedgerExecution measures end-to-end execution including idempotency key tracking.
func BenchmarkLedgerExecution(b *testing.B) {
	engine := domain.NewLedgerEngine()
	engine.RegisterAccount(1001, 100_000_000_00)
	engine.RegisterAccount(1002, 100_000_000_00)

	entries := []domain.LedgerEntry{
		{AccountID: 1001, Amount: 500, Type: domain.Debit},
		{AccountID: 1002, Amount: 500, Type: domain.Credit},
	}

	keys := make([]string, b.N)
	for i := range keys {
		keys[i] = fmt.Sprintf("bench-key-%d", i)
	}

	b.ResetTimer()
	b.ReportAllocs()

	for i := range b.N {
		tx := domain.Transaction{
			ID:             "tx-bench",
			IdempotencyKey: keys[i],
			Entries:        entries,
		}
		if err := engine.ExecuteTransaction(tx); err != nil {
			b.Fatalf("transaction failed: %v", err)
		}
	}
}

// BenchmarkParallelLedgerExecution measures multi-goroutine execution across distinct idempotency-free transfers.
func BenchmarkParallelLedgerExecution(b *testing.B) {
	engine := domain.NewLedgerEngine()
	engine.RegisterAccount(2001, 500_000_000_00)
	engine.RegisterAccount(2002, 500_000_000_00)

	b.ReportAllocs()
	b.RunParallel(func(pb *testing.PB) {
		entries := []domain.LedgerEntry{
			{AccountID: 2001, Amount: 100, Type: domain.Debit},
			{AccountID: 2002, Amount: 100, Type: domain.Credit},
		}
		for pb.Next() {
			_ = engine.ExecuteTransaction(domain.Transaction{
				ID:      "parallel-bench",
				Entries: entries,
			})
		}
	})
}
