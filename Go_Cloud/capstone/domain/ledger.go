package domain

import (
	"errors"
	"fmt"
	"sync"
	"sync/atomic"
	"time"
)

// Standard double-entry financial errors
var (
	ErrUnbalancedTransaction = errors.New("ledger transaction entries do not sum to zero (double-entry violated)")
	ErrDuplicateIdempotency  = errors.New("idempotency key already processed")
	ErrInsufficientFunds     = errors.New("source account has insufficient balance")
	ErrInvalidEntryAmount    = errors.New("entry amount cannot be zero")
	ErrAccountFrozen         = errors.New("account is frozen or suspended")
)

// Account represents a financial ledger entity.
// Memory layout optimized for 64-bit cache line alignment (56 bytes, 0 internal padding).
type Account struct {
	ID        int64     // 8 bytes
	Balance   int64     // 8 bytes (stored in minor currency units / cents to avoid float inaccuracy)
	Version   int64     // 8 bytes (optimistic concurrency version)
	CreatedAt time.Time // 24 bytes
	IsActive  bool      // 1 byte
	// 7 bytes padding at tail to align struct to 8-byte boundary
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
	Amount    int64 // In minor currency units (cents, satoshis). Must be > 0
	Type      EntryType
}

// Transaction represents a financial double-entry transaction.
type Transaction struct {
	ID             string
	IdempotencyKey string
	Entries        []LedgerEntry
	CreatedAt      time.Time
}

// LedgerEngine handles concurrent double-entry bookkeeping with strict ACID semantics
// and in-memory idempotency validation.
type LedgerEngine struct {
	mu           sync.RWMutex
	accounts     map[int64]*Account
	processedKey sync.Map // Idempotency cache: key -> timestamp
	txCounter    uint64
}

// NewLedgerEngine initializes a high-performance ledger engine.
func NewLedgerEngine() *LedgerEngine {
	return &LedgerEngine{
		accounts: make(map[int64]*Account, 10000),
	}
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

// ExecuteTransaction validates and executes double-entry bookkeeping.
// Guarantees:
// 1. Double-entry rule: Sum of debits + sum of credits == 0.
// 2. Strict idempotency: Replays of identical idempotency keys return early.
// 3. Atomicity: All account updates succeed, or none do.
func (le *LedgerEngine) ExecuteTransaction(tx Transaction) error {
	// 1. Idempotency Check
	if tx.IdempotencyKey != "" {
		if _, loaded := le.processedKey.LoadOrStore(tx.IdempotencyKey, time.Now().UnixNano()); loaded {
			return ErrDuplicateIdempotency
		}
	}

	// 2. Double-Entry Invariant Validation: Sum must be exactly zero
	var netSum int64
	for _, entry := range tx.Entries {
		if entry.Amount <= 0 {
			return ErrInvalidEntryAmount
		}
		netSum += entry.Amount * int64(entry.Type)
	}

	if netSum != 0 {
		return fmt.Errorf("%w: net delta is %d", ErrUnbalancedTransaction, netSum)
	}

	// 3. State Mutation under Mutex Lock
	le.mu.Lock()
	defer le.mu.Unlock()

	// Verify all accounts exist and source has sufficient balance
	for _, entry := range tx.Entries {
		acc, exists := le.accounts[entry.AccountID]
		if !exists {
			return fmt.Errorf("account %d not found", entry.AccountID)
		}
		if !acc.IsActive {
			return fmt.Errorf("%w: account %d", ErrAccountFrozen, entry.AccountID)
		}
		if entry.Type == Debit && acc.Balance < entry.Amount {
			return fmt.Errorf("%w: account %d has %d, required %d", ErrInsufficientFunds, entry.AccountID, acc.Balance, entry.Amount)
		}
	}

	// Apply balance mutations atomically
	for _, entry := range tx.Entries {
		acc := le.accounts[entry.AccountID]
		delta := entry.Amount * int64(entry.Type)
		acc.Balance += delta
		acc.Version++
	}

	atomic.AddUint64(&le.txCounter, 1)
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
	return atomic.LoadUint64(&le.txCounter)
}
