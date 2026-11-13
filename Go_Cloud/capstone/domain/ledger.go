package domain

import (
	"errors"
	"fmt"
	"sync"
	"sync/atomic"
	"time"
)

// Standard double-entry financial errors.
var (
	ErrUnbalancedTransaction = errors.New("ledger transaction entries do not sum to zero (double-entry violated)")
	ErrDuplicateIdempotency  = errors.New("idempotency key already processed")
	ErrInsufficientFunds     = errors.New("source account has insufficient balance")
	ErrInvalidEntryAmount    = errors.New("entry amount must be greater than zero")
	ErrEmptyTransaction      = errors.New("transaction must contain at least two entries")
	ErrAccountFrozen         = errors.New("account is frozen or suspended")
)

// Account represents a financial ledger entity.
// Memory layout aligned for 64-bit architectures (56 bytes, 0 interior padding).
type Account struct {
	ID        int64     // 8 bytes
	Balance   int64     // 8 bytes (minor currency units / cents to avoid float inaccuracy)
	Version   int64     // 8 bytes (optimistic concurrency version)
	CreatedAt time.Time // 24 bytes
	IsActive  bool      // 1 byte (+ 7 bytes tail padding)
}

// EntryType defines debit or credit.
type EntryType int8

const (
	Debit  EntryType = -1
	Credit EntryType = 1
)

// LedgerEntry represents an individual debit or credit leg.
type LedgerEntry struct {
	AccountID int64
	Amount    int64 // Minor currency units (cents, satoshis). Must be > 0.
	Type      EntryType
}

// Transaction represents a financial double-entry transaction.
type Transaction struct {
	ID             string
	IdempotencyKey string
	Entries        []LedgerEntry
	CreatedAt      time.Time
}

const defaultIdempotencyCap = 100_000

// LedgerEngine handles concurrent double-entry bookkeeping with strict ACID-like
// atomicity and bounded in-memory idempotency deduplication.
type LedgerEngine struct {
	mu             sync.RWMutex
	accounts       map[int64]*Account
	processedKeys  map[string]struct{}
	keyOrder       []string
	idempotencyCap int
	txCounter      atomic.Uint64
}

// NewLedgerEngine initializes a high-performance ledger engine.
func NewLedgerEngine() *LedgerEngine {
	return &LedgerEngine{
		accounts:       make(map[int64]*Account, 1024),
		processedKeys:  make(map[string]struct{}, 1024),
		keyOrder:       make([]string, 0, 1024),
		idempotencyCap: defaultIdempotencyCap,
	}
}

// ValidateDoubleEntry verifies entry amounts and double-entry balance with zero heap allocations.
func ValidateDoubleEntry(entries []LedgerEntry) error {
	if len(entries) < 2 {
		return ErrEmptyTransaction
	}
	var netSum int64
	for _, entry := range entries {
		if entry.Amount <= 0 {
			return ErrInvalidEntryAmount
		}
		if entry.Type != Debit && entry.Type != Credit {
			return fmt.Errorf("invalid entry type %d", entry.Type)
		}
		netSum += entry.Amount * int64(entry.Type)
	}
	if netSum != 0 {
		return fmt.Errorf("%w: net delta is %d", ErrUnbalancedTransaction, netSum)
	}
	return nil
}

// RegisterAccount creates or initializes an account in the ledger.
func (le *LedgerEngine) RegisterAccount(id int64, initialBalance int64) *Account {
	le.mu.Lock()
	defer le.mu.Unlock()

	acc := &Account{
		ID:        id,
		Balance:   initialBalance,
		Version:   1,
		CreatedAt: time.Now(),
		IsActive:  true,
	}
	le.accounts[id] = acc
	return acc
}

// ExecuteTransaction validates and executes double-entry bookkeeping atomically.
// Guarantees:
// 1. Double-entry rule: Sum of debits + sum of credits == 0.
// 2. Idempotency: Validated BEFORE recording the key so failed requests can be retried.
// 3. Atomicity: All account updates succeed, or none do.
func (le *LedgerEngine) ExecuteTransaction(tx Transaction) error {
	if err := ValidateDoubleEntry(tx.Entries); err != nil {
		return err
	}

	le.mu.Lock()
	defer le.mu.Unlock()

	if tx.IdempotencyKey != "" {
		if _, exists := le.processedKeys[tx.IdempotencyKey]; exists {
			return ErrDuplicateIdempotency
		}
	}

	// Verify all accounts exist, are active, and debited accounts have sufficient funds
	// (including aggregated debits if the same account appears multiple times in one tx).
	projected := make(map[int64]int64, len(tx.Entries))
	for _, entry := range tx.Entries {
		acc, exists := le.accounts[entry.AccountID]
		if !exists {
			return fmt.Errorf("account %d not found", entry.AccountID)
		}
		if !acc.IsActive {
			return fmt.Errorf("%w: account %d", ErrAccountFrozen, entry.AccountID)
		}
		bal, seen := projected[entry.AccountID]
		if !seen {
			bal = acc.Balance
		}
		bal += entry.Amount * int64(entry.Type)
		if bal < 0 {
			return fmt.Errorf("%w: account %d has %d, required %d", ErrInsufficientFunds, entry.AccountID, acc.Balance, entry.Amount)
		}
		projected[entry.AccountID] = bal
	}

	// Apply balance mutations atomically only after every precondition passes.
	for _, entry := range tx.Entries {
		acc := le.accounts[entry.AccountID]
		acc.Balance += entry.Amount * int64(entry.Type)
		acc.Version++
	}

	// Record idempotency key AFTER commit, evicting oldest entries when over cap.
	if tx.IdempotencyKey != "" {
		if len(le.keyOrder) >= le.idempotencyCap {
			oldest := le.keyOrder[0]
			le.keyOrder = le.keyOrder[1:]
			delete(le.processedKeys, oldest)
		}
		le.processedKeys[tx.IdempotencyKey] = struct{}{}
		le.keyOrder = append(le.keyOrder, tx.IdempotencyKey)
	}

	le.txCounter.Add(1)
	return nil
}

// GetAccount returns a snapshot copy of the account balance and state.
func (le *LedgerEngine) GetAccount(id int64) (Account, bool) {
	le.mu.RLock()
	defer le.mu.RUnlock()

	acc, exists := le.accounts[id]
	if !exists {
		return Account{}, false
	}
	return *acc, true
}

// TotalTransactionsProcessed returns cumulative executed transactions.
func (le *LedgerEngine) TotalTransactionsProcessed() uint64 {
	return le.txCounter.Load()
}
