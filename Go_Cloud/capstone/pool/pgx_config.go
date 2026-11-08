package pool

import (
	"context"
	"fmt"
	"time"
)

// PgxPoolSettings encapsulates senior-level database connection pool parameters
// calibrated for high-throughput Go microservices in Kubernetes.
type PgxPoolSettings struct {
	MaxConns          int32
	MinConns          int32
	MaxConnLifetime   time.Duration
	MaxConnIdleTime   time.Duration
	HealthCheckPeriod time.Duration
	ConnectTimeout    time.Duration
}

// DefaultProductionSettings provides battle-tested connection pool parameters.
// Why these values matter:
// - MaxConns (50): Prevents PostgreSQL backend process memory bloat and connection starvation.
// - MinConns (10): Keeps warm connections ready to absorb traffic spikes without TCP/TLS handshake latency.
// - MaxConnLifetime (1 hour): Avoids DNS drift when database endpoints fail over (e.g. AWS RDS Multi-AZ).
// - HealthCheckPeriod (30s): Proactively detects dead TCP connections severed by cloud firewalls or NAT gateways.
func DefaultProductionSettings() PgxPoolSettings {
	return PgxPoolSettings{
		MaxConns:          50,
		MinConns:          10,
		MaxConnLifetime:   1 * time.Hour,
		MaxConnIdleTime:   15 * time.Minute,
		HealthCheckPeriod: 30 * time.Second,
		ConnectTimeout:    5 * time.Second,
	}
}

// ValidatePoolConfig validates pool constraints against common Kubernetes anti-patterns.
func ValidatePoolConfig(s PgxPoolSettings, containerCPUQuota float64) error {
	if s.MinConns > s.MaxConns {
		return fmt.Errorf("minConns (%d) cannot exceed maxConns (%d)", s.MinConns, s.MaxConns)
	}

	// Heuristic: If CPU quota is small (e.g. 1 core) and MaxConns is huge (>100),
	// goroutines will suffer severe lock contention waiting on database connection checkouts.
	if containerCPUQuota > 0 && float64(s.MaxConns)/containerCPUQuota > 50 {
		return fmt.Errorf("danger: maxConns (%d) is disproportionately large for %.1f CPU cores, causing severe pool mutex contention", s.MaxConns, containerCPUQuota)
	}

	return nil
}
