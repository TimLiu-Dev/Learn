package slicesstructs

import "testing"

func TestAppendIsolated_NoAliasing(t *testing.T) {
	backing := make([]int, 2, 8)
	backing[0], backing[1] = 10, 20

	branchA := AppendIsolated(backing, 30)
	branchB := AppendIsolated(backing, 99)

	if len(branchA) != 3 || branchA[2] != 30 {
		t.Fatalf("branchA = %v, want [10 20 30]", branchA)
	}
	if len(branchB) != 3 || branchB[2] != 99 {
		t.Fatalf("branchB = %v, want [10 20 99]", branchB)
	}
	// Crucial check: branchB must NOT overwrite branchA[2] via shared capacity!
	if branchA[2] != 30 {
		t.Fatalf("slice aliasing detected: branchA[2] mutated to %d", branchA[2])
	}
}

func TestLookupPort(t *testing.T) {
	if port, ok := LookupPort(nil, "api"); ok || port != 0 {
		t.Fatalf("nil map lookup returned (%d, %v), want (0, false)", port, ok)
	}
	ports := map[string]int{"api": 8080, "disabled": 0}
	if port, ok := LookupPort(ports, "disabled"); !ok || port != 0 {
		t.Fatalf("zero-value key lookup returned (%d, %v), want (0, true)", port, ok)
	}
}

func TestCounter_PointerReceiver(t *testing.T) {
	var c Counter
	c.Inc()
	c.Inc()
	if c.Value() != 2 {
		t.Fatalf("expected Value() == 2, got %d", c.Value())
	}
}
