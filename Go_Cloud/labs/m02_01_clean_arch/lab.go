package orders

import (
	"context"
	"errors"
)

var ErrInvalidAmount = errors.New("amount must be greater than zero")

// PaymentGateway is a consumer-owned port defined where it is used, not in the adapter.
type PaymentGateway interface {
	Charge(ctx context.Context, orderID string, amountCents int64) error
}

type OrderProcessor struct {
	gateway PaymentGateway
}

func NewOrderProcessor(gateway PaymentGateway) *OrderProcessor {
	return &OrderProcessor{gateway: gateway}
}

func (p *OrderProcessor) ProcessOrder(ctx context.Context, orderID string, amountCents int64) error {
	// TODO: Validate amountCents > 0 (returning ErrInvalidAmount otherwise), then call p.gateway.Charge.
	return nil
}
