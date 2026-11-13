package slicesstructs

// AppendIsolated appends extra values to base WITHOUT mutating base's underlying backing array,
// even when cap(base) > len(base).
func AppendIsolated(base []int, extra ...int) []int {
	// TODO: Use full slice expression base[:len(base):len(base)] or copy into a new slice.
	return nil
}

// LookupPort looks up a service name in ports using Go's comma-ok map idiom.
// Returns (0, false) if ports is nil or service is missing.
func LookupPort(ports map[string]int, service string) (int, bool) {
	// TODO: Implement map lookup with comma-ok.
	return 0, false
}

// Counter demonstrates value vs. pointer receivers in Go.
type Counter struct {
	n int
}

// Inc increments the counter in place (must mutate the receiver).
func (c *Counter) Inc() {
	// TODO: Increment c.n.
}

// Value returns the current counter value.
func (c Counter) Value() int {
	return c.n
}
