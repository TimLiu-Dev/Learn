package orders

import (
	"context"
	"errors"
	"testing"
)

type mockGateway struct {
	chargedID string
	amount    int64
	err       error
}

func (m *mockGateway) Charge(ctx context.Context, orderID string, amountCents int64) error {
	m.chargedID = orderID
	m.amount = amountCents
	return m.err
}

func TestOrderProcessor(t *testing.T) {
	gw := &mockGateway{}
	proc := NewOrderProcessor(gw)

	if err := proc.ProcessOrder(context.Background(), "ord-1", 0); !errors.Is(err, ErrInvalidAmount) {
		t.Fatalf("expected ErrInvalidAmount for 0 cents, got %v", err)
	}
	if gw.chargedID != "" {
		t.Fatal("gateway must not be called for invalid amounts")
	}

	if err := proc.ProcessOrder(context.Background(), "ord-2", 4999); err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if gw.chargedID != "ord-2" || gw.amount != 4999 {
		t.Fatalf("mock gateway saw (%q, %d), want (ord-2, 4999)", gw.chargedID, gw.amount)
	}
}
