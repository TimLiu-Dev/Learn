class Backend(val name: String) {
    var healthy = true
    var active = 0      // requests in flight right now
    var served = 0      // requests sent here so far
}

enum class Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

class Balancer(private val backends: List<Backend>, private val strategy: Strategy) {
    private var next = 0

    // Chooses well, but never counts the request as in flight.
    fun pick(): Backend? {
        val up = backends.filter { it.healthy }
        if (up.isEmpty()) return null
        val b = when (strategy) {
            Strategy.ROUND_ROBIN -> up[Math.floorMod(next++, up.size)]
            Strategy.LEAST_CONNECTIONS -> up.minBy { it.active }
        }
        b.served++
        return b
    }

    fun finish(b: Backend) { b.active-- }
}
