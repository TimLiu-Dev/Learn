package mentalmodel

import "strings"

// ServiceConfig demonstrates Go zero values and Go 1.26 pointer initialization with new(expr).
type ServiceConfig struct {
	Host       string
	Port       int
	MaxRetries *int
	TLSEnabled bool
}

// DefaultConfig returns a ServiceConfig with Host "0.0.0.0", Port 8080,
// TLSEnabled true, and MaxRetries initialized to 3 using new(3) (Go 1.26+).
func DefaultConfig() ServiceConfig {
	// TODO: Return populated ServiceConfig. Note: new(3) creates *int pointing to 3 in Go 1.26+.
	return ServiceConfig{}
}

// ParseModuleName extracts the final path segment (e.g., "github.com/acme/ledger" -> "ledger").
// Returns ("", false) if the input is empty or ends with '/'.
func ParseModuleName(importPath string) (string, bool) {
	// TODO: Use strings.LastIndex or strings.Split; return (name, true) or ("", false).
	_ = strings.TrimSpace
	return "", false
}
