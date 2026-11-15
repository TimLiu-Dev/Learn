class Backend(val name: String) {
    var healthy = true
    var active = 0      // requests in flight right now
    var served = 0      // requests sent here so far
}

enum class Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

class Balancer(private val backends: List<Backend>, private val strategy: Strategy) {
    private var next = 0

    // Choose a healthy backend, count the request on it, and return it (null if none is healthy).
    fun pick(): Backend? {
        val up = backends.filter { it.healthy }
        if (up.isEmpty()) return null
        val b = when (strategy) {
            Strategy.ROUND_ROBIN -> up[Math.floorMod(next++, up.size)]
            Strategy.LEAST_CONNECTIONS -> up.minBy { it.active }   // minBy keeps the first of equals
        }
        b.active++          // least-connections only works if picks are counted
        b.served++
        return b
    }

    // A request on b has finished.
    fun finish(b: Backend) { b.active-- }
}
