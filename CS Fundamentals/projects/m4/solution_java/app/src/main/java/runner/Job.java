package runner;

import java.util.List;

/** One simulated job: {@code durationMs} of interruptible "work"; the first {@code fail} attempts fail. */
public record Job(String id, long durationMs, int fail, List<String> deps) {
    public Job {
        deps = List.copyOf(deps);
    }

    /** The delay before attempt k+1, after attempt k failed: 50 ms × 2^(k−1). */
    public static long backoffMs(int failedAttempt) {
        return 50L << (failedAttempt - 1);
    }
}
