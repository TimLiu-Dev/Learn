package memorylayout

import (
	"errors"
	"testing"
	"unsafe"
)

func TestTelemetryPacketLayout(t *testing.T) {
	if got := unsafe.Sizeof(TelemetryPacket{}); got != 24 {
		t.Fatalf("unsafe.Sizeof(TelemetryPacket{}) = %d bytes, want 24 bytes (reorder largest fields first)", got)
	}
}

func TestParseAndProcess_ZeroAlloc(t *testing.T) {
	src := []byte("sensor=42,temp=36.5")
	dst := make([]byte, 0, 64)
	pkt := TelemetryPacket{DeviceID: 42, Timestamp: 1700000000, Priority: 1, IsAlert: true}

	n, err := ParseAndProcess(pkt, src, dst)
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if n != len(src) || string(dst[:n]) != string(src) {
		t.Fatalf("copied %d bytes (%q), want %d (%q)", n, dst[:n], len(src), src)
	}

	if _, err := ParseAndProcess(pkt, src, make([]byte, 0, 4)); !errors.Is(err, ErrBufferTooSmall) {
		t.Fatalf("expected ErrBufferTooSmall, got %v", err)
	}

	allocs := testing.AllocsPerRun(100, func() {
		_, _ = ParseAndProcess(pkt, src, dst)
	})
	if allocs != 0 {
		t.Fatalf("ParseAndProcess allocated %.1f times per call; want 0", allocs)
	}
}
