package ingestor

import (
	"context"
	"encoding/binary"
	"errors"
)

type Sample struct {
	Seq      uint8
	SpeedKmh float32
	RPM      uint16
}

func DecodeFrame(pkt []byte) (Sample, error) {
	if len(pkt) != 16 || pkt[0] != 0xAA || pkt[1] != 0x55 {
		return Sample{}, errors.New("invalid telemetry frame header or length")
	}
	scaled := binary.BigEndian.Uint16(pkt[4:6])
	rpm := binary.BigEndian.Uint16(pkt[6:8])
	return Sample{
		Seq:      pkt[2],
		SpeedKmh: float32(scaled) / 100.0,
		RPM:      rpm,
	}, nil
}

func RunBatchIngestor(ctx context.Context, ch <-chan []byte, maxBatch int, flush func([]Sample)) int {
	seen := make(map[uint8]bool)
	batch := make([]Sample, 0, maxBatch)
	accepted := 0

	flushPending := func() {
		if len(batch) > 0 {
			out := make([]Sample, len(batch))
			copy(out, batch)
			flush(out)
			batch = batch[:0]
		}
	}

	for {
		select {
		case <-ctx.Done():
			flushPending()
			return accepted
		case raw, ok := <-ch:
			if !ok {
				flushPending()
				return accepted
			}
			s, err := DecodeFrame(raw)
			if err != nil || seen[s.Seq] {
				continue
			}
			seen[s.Seq] = true
			accepted++
			batch = append(batch, s)
			if len(batch) >= maxBatch {
				flushPending()
			}
		}
	}
}
