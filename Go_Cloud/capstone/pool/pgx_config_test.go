package pool

import (
	"testing"
	"time"
)

func TestValidatePoolConfig(t *testing.T) {
	valid := DefaultProductionSettings()

	tests := []struct {
		name    string
		cfg     PgxPoolSettings
		quota   float64
		wantErr bool
	}{
		{name: "valid production defaults on 2 cores", cfg: valid, quota: 2.0, wantErr: false},
		{name: "min exceeds max", cfg: PgxPoolSettings{MinConns: 60, MaxConns: 50}, quota: 2.0, wantErr: true},
		{name: "non-positive max", cfg: PgxPoolSettings{MinConns: 0, MaxConns: 0}, quota: 2.0, wantErr: true},
		{name: "oversized pool for 1 CPU core", cfg: PgxPoolSettings{MinConns: 5, MaxConns: 100}, quota: 1.0, wantErr: true},
		{name: "custom valid pool", cfg: PgxPoolSettings{MinConns: 4, MaxConns: 25, MaxConnLifetime: time.Hour}, quota: 1.0, wantErr: false},
	}

	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			err := ValidatePoolConfig(tc.cfg, tc.quota)
			if (err != nil) != tc.wantErr {
				t.Fatalf("ValidatePoolConfig() error = %v, wantErr = %v", err, tc.wantErr)
			}
		})
	}
}
