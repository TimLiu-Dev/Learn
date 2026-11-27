package runner

/** Orders jobs so every job comes after its dependencies. */
object Plan {
    // Milestone 4: a topological order (L21), and an InputError("dependency cycle: a -> c -> b -> a").
    // Until then, file order is fine: milestones 1–3 have no dependencies.
    fun order(jobs: List<Job>): List<Job> = jobs
}
