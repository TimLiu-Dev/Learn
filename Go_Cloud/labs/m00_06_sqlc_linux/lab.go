package sqlclinux

import (
	"context"
	"errors"
	"strconv"
	"strings"
)

var ErrNotFound = errors.New("account not found")

type AccountRow struct {
	ID      int64
	Balance int64
}

// Queries models sqlc-generated typed query methods backed by a parameterized DBTX.
type DBTX interface {
	QueryRowContext(ctx context.Context, query string, args ...any) (AccountRow, error)
}

type Queries struct {
	db DBTX
}

func NewQueries(db DBTX) *Queries {
	return &Queries{db: db}
}

// GetAccountByID demonstrates sqlc-style typed query wrappers using $1 placeholders (never fmt.Sprintf SQL!).
func (q *Queries) GetAccountByID(ctx context.Context, id int64) (AccountRow, error) {
	// TODO: Call q.db.QueryRowContext(ctx, "SELECT id, balance FROM accounts WHERE id = $1", id)
	return AccountRow{}, ErrNotFound
}

// ParseCgroupMaxCores parses Linux cgroups v2 cpu.max content (e.g. "200000 100000" -> 2.0 cores, "max 100000" -> 0 meaning unlimited).
func ParseCgroupMaxCores(cpuMax string) (float64, error) {
	// TODO: Parse "<quota> <period>". Return 0, nil if quota == "max".
	_ = strconv.ParseFloat
	_ = strings.Fields
	return 0, errors.New("not implemented")
}
