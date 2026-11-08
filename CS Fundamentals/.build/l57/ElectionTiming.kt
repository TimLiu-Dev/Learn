// Exercise: how much randomness does the election timeout need? (compile with Election.kt)
// Like the Raft paper's Figure 16: crash the leader at a random point in its heartbeat
// interval, then measure how long the cluster has no leader.

fun downtime(seed: Long, min: Int, max: Int): Long {
    val sim = Sim(seed, timeoutMin = min, timeoutMax = max, heartbeatMs = min / 2, tracing = false)
    sim.start()
    while (sim.leader() == null && sim.now < 10_000) sim.runUntil(sim.now + 1)
    if (sim.leader() == null) return 10_000                // never elected a first leader in 10 s
    sim.runUntil(sim.now + 500)                            // let it settle
    sim.runUntil(sim.now + sim.rnd.nextInt(min / 2))
    val old = sim.leader()!!                               // a healthy cluster keeps its leader
    sim.crash(old.id)
    val crashedAt = sim.now
    while (sim.now < crashedAt + 10_000) {
        sim.runUntil(sim.now + 1)
        if (sim.leader() != null) return sim.now - crashedAt
    }
    return 10_000                                          // gave up: still no leader after 10 s
}

fun main() {
    println("%-14s %8s %8s %10s".format("timeout", "median", "worst", "over 10 s"))
    for ((min, max) in listOf(150 to 150, 150 to 151, 150 to 155, 150 to 175, 150 to 200, 150 to 300)) {
        val times = (1L..1_000L).map { downtime(it, min, max) }.sorted()
        println("%-14s %6d ms %6d ms %10d".format("$min-$max ms", times[times.size / 2], times.last(),
            times.count { it >= 10_000 }))
    }
}
