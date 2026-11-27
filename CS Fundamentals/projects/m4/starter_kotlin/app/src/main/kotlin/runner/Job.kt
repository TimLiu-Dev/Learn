package runner

/** One simulated job: [durationMs] of interruptible "work"; the first [fail] attempts fail. */
data class Job(val id: String, val durationMs: Long, val fail: Int = 0, val deps: List<String> = emptyList())

/** How a job ended. Every job ends exactly once, with exactly one of these. */
enum class Outcome(val event: String) { DONE("done"), GIVEUP("giveup"), SKIP("skip"), CANCEL("cancel"), DROP("drop") }

/** The delay before attempt k+1, after attempt k failed. */
fun backoffMs(failedAttempt: Int): Long = TODO("Milestone 3: 50 ms × 2^(k−1)")
