package ingestor

import (
	"context"
	"testing"
)

func TestBatchIngestorDedupAndFlush(t *testing.T) {
	ch := make(chan []byte, 4)
	pkt1 := []byte{0xAA, 0x55, 1, 1, 0x25, 0x1C, 0x0B, 0xB8, 4, 38, 0, 0, 0, 0, 0, 0}
	pktDup := append([]byte(nil), pkt1...)
	pkt2 := []byte{0xAA, 0x55, 2, 1, 0x2E, 0xE0, 0x0D, 0xAC, 4, 39, 0, 0, 0, 0, 0, 0}

	ch <- pkt1
	ch <- pktDup
	ch <- pkt2
	close(ch)

	var total []Sample
	count := RunBatchIngestor(context.Background(), ch, 2, func(b []Sample) {
		total = append(total, b...)
	})

	if count != 2 || len(total) != 2 {
		t.Fatalf("expected 2 deduplicated samples, got count=%d len=%d", count, len(total))
	}
	if total[0].SpeedKmh != 95.0 || total[1].SpeedKmh != 120.0 {
		t.Fatalf("unexpected decoded speeds: %+v", total)
	}
}
