package runner

/** Orders jobs so every job comes after its dependencies (L21's topological sort). */
object Plan {
    /**
     * Kahn's algorithm. The ready queue starts with the jobs that have no dependencies,
     * in file order, and stays FIFO, so ties are broken by file order and the result is
     * the same on every run.
     */
    fun order(jobs: List<Job>): List<Job> {
        val byId = jobs.associateBy { it.id }
        val missing = jobs.associate { it.id to it.deps.toSet().size }.toMutableMap()
        val dependents = jobs.associate { it.id to mutableListOf<Job>() }
        for (job in jobs) for (dep in job.deps.toSet()) dependents.getValue(dep) += job
        val ready = ArrayDeque(jobs.filter { missing[it.id] == 0 })
        val order = mutableListOf<Job>()
        while (ready.isNotEmpty()) {
            val job = ready.removeFirst()
            order += job
            for (next in dependents.getValue(job.id)) {
                val left = missing.getValue(next.id) - 1
                missing[next.id] = left
                if (left == 0) ready.addLast(next)
            }
        }
        if (order.size < jobs.size) {
            val stuck = jobs.filter { missing.getValue(it.id) > 0 }
            throw InputError("dependency cycle: " + findCycle(stuck, byId).joinToString(" -> "))
        }
        return order
    }

    /** Every stuck job has a stuck dependency, so walking deps must revisit a job: that's the cycle. */
    private fun findCycle(stuck: List<Job>, byId: Map<String, Job>): List<String> {
        val stuckIds = stuck.map { it.id }.toSet()
        val path = mutableListOf<String>()
        var current = stuck.first().id
        while (current !in path) {
            path += current
            current = byId.getValue(current).deps.first { it in stuckIds }
        }
        return path.subList(path.indexOf(current), path.size) + current
    }
}
