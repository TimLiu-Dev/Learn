class Backend(val name: String) {
    var healthy = true
    var active = 0      // requests in flight right now
    var served = 0      // requests sent here so far
}

enum class Strategy { ROUND_ROBIN, LEAST_CONNECTIONS }

class Balancer(private val backends: List<Backend>, private val strategy: Strategy) {
    private var next = 0

    // Choose a healthy backend, count the request on it, and return it (null if none is healthy).
    fun pick(): Backend? = TODO("keep the healthy backends, choose one by strategy, then count the request on it")

    // A request on b has finished.
    fun finish(b: Backend) { b.active-- }
}
