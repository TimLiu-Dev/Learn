package memorylayout

import "errors"

var ErrBufferTooSmall = errors.New("destination buffer capacity insufficient")

// TelemetryPacket starts unaligned (32 bytes on 64-bit architectures).
// TODO: Reorder the fields so unsafe.Sizeof(TelemetryPacket{}) == 24 bytes.
type TelemetryPacket struct {
	Priority  uint8
	DeviceID  int64
	IsAlert   bool
	Timestamp int64
}

// ParseAndProcess copies src into dst[:len(src)] with ZERO heap allocations.
// Return ErrBufferTooSmall if cap(dst) < len(src).
func ParseAndProcess(packet TelemetryPacket, src []byte, dst []byte) (int, error) {
	// TODO: Check capacity and copy src into dst[:len(src)] without calling make().
	return 0, nil
}
