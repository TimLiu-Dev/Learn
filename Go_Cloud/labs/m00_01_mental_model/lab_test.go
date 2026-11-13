package mentalmodel

import "testing"

func TestDefaultConfig(t *testing.T) {
	cfg := DefaultConfig()
	if cfg.Host != "0.0.0.0" {
		t.Fatalf("expected Host 0.0.0.0, got %q", cfg.Host)
	}
	if cfg.Port != 8080 {
		t.Fatalf("expected Port 8080, got %d", cfg.Port)
	}
	if !cfg.TLSEnabled {
		t.Fatal("expected TLSEnabled to be true")
	}
	if cfg.MaxRetries == nil || *cfg.MaxRetries != 3 {
		t.Fatalf("expected *MaxRetries == 3, got %v", cfg.MaxRetries)
	}
}

func TestParseModuleName(t *testing.T) {
	tests := []struct {
		input  string
		want   string
		wantOK bool
	}{
		{"github.com/acme/ledger", "ledger", true},
		{"ledger", "ledger", true},
		{"", "", false},
		{"github.com/acme/", "", false},
	}

	for _, tc := range tests {
		got, ok := ParseModuleName(tc.input)
		if got != tc.want || ok != tc.wantOK {
			t.Fatalf("ParseModuleName(%q) = (%q, %v), want (%q, %v)", tc.input, got, ok, tc.want, tc.wantOK)
		}
	}
}
