package sqlclinux

import (
	"context"
	"errors"
	"strings"
	"testing"
)

type stubDB struct {
	lastQuery string
	lastArgs  []any
	row       AccountRow
	err       error
}

func (s *stubDB) QueryRowContext(ctx context.Context, query string, args ...any) (AccountRow, error) {
	s.lastQuery = query
	s.lastArgs = args
	return s.row, s.err
}

func TestQueries_ParameterizedSQL(t *testing.T) {
	db := &stubDB{row: AccountRow{ID: 7, Balance: 9000}}
	q := NewQueries(db)

	row, err := q.GetAccountByID(context.Background(), 7)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if row.Balance != 9000 {
		t.Fatalf("expected 9000, got %d", row.Balance)
	}
	if !strings.Contains(db.lastQuery, "$1") || len(db.lastArgs) != 1 || db.lastArgs[0] != int64(7) {
		t.Fatalf("query must use parameterized $1 placeholder; got query=%q args=%v", db.lastQuery, db.lastArgs)
	}
}

func TestParseCgroupMaxCores(t *testing.T) {
	tests := []struct {
		raw     string
		want    float64
		wantErr bool
	}{
		{"200000 100000\n", 2.0, false},
		{"50000 100000\n", 0.5, false},
		{"max 100000\n", 0, false},
		{"invalid", 0, true},
	}

	for _, tc := range tests {
		got, err := ParseCgroupMaxCores(tc.raw)
		if (err != nil) != tc.wantErr {
			t.Fatalf("ParseCgroupMaxCores(%q) err=%v wantErr=%v", tc.raw, err, tc.wantErr)
		}
		if !tc.wantErr && got != tc.want {
			t.Fatalf("ParseCgroupMaxCores(%q) = %v, want %v", tc.raw, got, tc.want)
		}
	}
}

var _ = errors.Is
