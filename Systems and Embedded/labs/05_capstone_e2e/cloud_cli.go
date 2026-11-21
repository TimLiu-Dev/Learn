package main

import (
	"bufio"
	"cockpit/ingestor"
	"encoding/hex"
	"fmt"
	"os"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	seen := make(map[uint8]bool)
	count := 0
	for scanner.Scan() {
		raw, err := hex.DecodeString(scanner.Text())
		if err != nil {
			os.Exit(1)
		}
		s, err := ingestor.DecodeFrame(raw)
		if err != nil {
			fmt.Fprintf(os.Stderr, "drop frame: %v\n", err)
			continue
		}
		if seen[s.Seq] {
			continue
		}
		seen[s.Seq] = true
		count++
		fmt.Printf("[Go Cloud Ingestor] seq=%d speed=%.2f km/h rpm=%d\n", s.Seq, s.SpeedKmh, s.RPM)
	}
	if count != 3 {
		fmt.Fprintf(os.Stderr, "expected 3 frames, got %d\n", count)
		os.Exit(1)
	}
	fmt.Println("E2E Pipeline PASS: C11 MCU -> Rust CRC/VHAL Validator -> Go Cloud Ingestor.")
}
