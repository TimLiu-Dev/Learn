// Three instances, a database that is down from t=10 s to t=20 s, and an orchestrator
// that probes every second and restarts an instance after 3 failed liveness checks in a row.
// Starting an instance takes 20 s. Everything is simulated, one step per second.
class Instance(val name: String) {
    var startingUntil = 0          // not serving before this second
    var liveFailures = 0
}

fun simulate(liveChecksDb: Boolean): String {
    val instances = listOf(Instance("a"), Instance("b"), Instance("c"))
    var restarts = 0; var dark = 0
    val events = mutableListOf<String>()
    for (t in 0 until 60) {
        val dbUp = t !in 10 until 20
        for (i in instances) {
            if (t < i.startingUntil) continue                         // still starting: not probed yet
            val live = if (liveChecksDb) dbUp else true                // /live
            i.liveFailures = if (live) 0 else i.liveFailures + 1
            if (i.liveFailures == 3) {
                i.startingUntil = t + 20; i.liveFailures = 0; restarts++
                events += "t=$t restart ${i.name}"
            }
        }
        val ready = instances.count { t >= it.startingUntil && dbUp }  // /ready checks the database
        if (ready == 0) dark++
    }
    return "${events.joinToString(", ").ifEmpty { "no restarts" }}\n  restarts: $restarts, seconds with no ready instance: $dark"
}

fun main() {
    println("database down from t=10 to t=20")
    println("/live checks the database:\n  " + simulate(liveChecksDb = true))
    println("/live checks only the process, /ready checks the database:\n  " + simulate(liveChecksDb = false))
}
