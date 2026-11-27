package runner

/**
 * Runs a plan of jobs and returns how many ended with each [Outcome].
 * Milestone 1: one job at a time. Milestone 2: N at once, a bounded queue between a producer
 * and the workers. Milestone 3: retries, backoff, timeouts. Milestone 4: dependencies.
 * Milestone 5: a clean stop on SIGTERM.
 */
class Runner(private val plan: List<Job>, private val opts: Options, private val events: Events) {
    suspend fun run(): Map<Outcome, Int> = TODO("Milestone 1: run each job (delay for its duration) and print its events")
}
