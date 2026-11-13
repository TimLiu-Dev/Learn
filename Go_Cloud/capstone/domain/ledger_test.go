package domain

import (
	"errors"
	"sync"
	"testing"
)

func TestExecuteTransaction_DoubleEntryAndRollback(t *testing.T) {
	le := NewLedgerEngine()
	le.RegisterAccount(1, 1000)
	le.RegisterAccount(2, 500)

	// Invalid/unbalanced transactions must fail AND must NOT poison their idempotency key.
	unbalanced := Transaction{
		IdempotencyKey: "k-retryable",
		Entries: []LedgerEntry{
			{AccountID: 1, Amount: 500, Type: Debit},
			{AccountID: 2, Amount: 400, Type: Credit},
		},
	}
	if err := le.ExecuteTransaction(unbalanced); !errors.Is(err, ErrUnbalancedTransaction) {
		t.Fatalf("expected ErrUnbalancedTransaction, got %v", err)
	}

	// Retrying with corrected payload using the same idempotency key must succeed!
	valid := Transaction{
		IdempotencyKey: "k-retryable",
		Entries: []LedgerEntry{
			{AccountID: 1, Amount: 300, Type: Debit},
			{AccountID: 2, Amount: 300, Type: Credit},
		},
	}
	if err := le.ExecuteTransaction(valid); err != nil {
		t.Fatalf("expected retry to succeed, got %v", err)
	}

	// Duplicate replay of a committed key must fail with ErrDuplicateIdempotency.
	if err := le.ExecuteTransaction(valid); !errors.Is(err, ErrDuplicateIdempotency) {
		t.Fatalf("expected ErrDuplicateIdempotency, got %v", err)
	}

	acc1, _ := le.GetAccount(1)
	acc2, _ := le.GetAccount(2)
	if acc1.Balance != 700 || acc2.Balance != 800 {
		t.Fatalf("unexpected balances: acc1=%d acc2=%d", acc1.Balance, acc2.Balance)
	}
}

func TestExecuteTransaction_ConcurrentRace(t *testing.T) {
	le := NewLedgerEngine()
	le.RegisterAccount(10, 100_000)
	le.RegisterAccount(20, 100_000)

	var wg sync.WaitGroup
	for i := range 200 {
		wg.Add(1)
		go func(n int) {
			defer wg.Done()
			from, to := int64(10), int64(20)
			if n%2 == 1 {
				from, to = 20, 10
			}
			_ = le.ExecuteTransaction(Transaction{
				Entries: []LedgerEntry{
					{AccountID: from, Amount: 10, Type: Debit},
					{AccountID: to, Amount: 10, Type: Credit},
				},
			})
		}(i)
	}
	wg.Wait()

	a1, _ := le.GetAccount(10)
	a2, _ := le.GetAccount(20)
	if a1.Balance+a2.Balance != 200_000 {
		t.Fatalf("conservation of money violated: %d + %d != 200000", a1.Balance, a2.Balance)
	}
}
