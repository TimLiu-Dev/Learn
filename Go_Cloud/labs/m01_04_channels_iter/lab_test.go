package pipeline

import (
	"context"
	"slices"
	"testing"
	"time"
)

func TestFanOutProcess_DrainAndCancel(t *testing.T) {
	jobs := make(chan int, 10)
	for i := 1; i <= 10; i++ {
		jobs <- i
	}
	close(jobs)

	out := FanOutProcess(context.Background(), jobs, 4, func(v int) int { return v * 2 })
	var results []int
	for v := range out {
		results = append(results, v)
	}
	slices.Sort(results)
	if len(results) != 10 || results[0] != 2 || results[9] != 20 {
		t.Fatalf("unexpected pipeline results: %v", results)
	}

	// Cancellation must unblock even if consumer stops reading and jobs remains open.
	ctx, cancel := context.WithCancel(context.Background())
	unclosedJobs := make(chan int)
	cancelledOut := FanOutProcess(ctx, unclosedJobs, 2, func(v int) int { return v })
	cancel()
	select {
	case _, ok := <-cancelledOut:
		if ok {
			t.Fatal("expected cancelled pipeline channel to close")
		}
	case <-time.After(time.Second):
		t.Fatal("pipeline leaked goroutines and did not close on ctx cancellation")
	}
}

func TestFilterSeq_EarlyBreak(t *testing.T) {
	nums := []int{1, 2, 3, 4, 5, 6}
	var got []int
	for v := range FilterSeq(nums, func(n int) bool { return n%2 == 0 }) {
		got = append(got, v)
		if len(got) == 2 {
			break
		}
	}
	if !slices.Equal(got, []int{2, 4}) {
		t.Fatalf("expected [2 4], got %v", got)
	}
}
