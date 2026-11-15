import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BalancerTest {
    private fun three() = listOf(Backend("b1"), Backend("b2"), Backend("b3"))
    private fun Balancer.names(n: Int) = (1..n).joinToString(" ") { pick()!!.name }

    // One request arrives per tick. Each backend serves one request at a time (like Part 1's
    // one-thread backends); b2 takes 20 ticks, b1 and b3 take 2. Returns "last finish tick | served".
    private fun simulate(strategy: Strategy): String {
        val bs = three()
        val cost = mapOf("b1" to 2, "b2" to 20, "b3" to 2)
        val lb = Balancer(bs, strategy)
        val busyUntil = mutableMapOf("b1" to 0, "b2" to 0, "b3" to 0)
        val running = mutableListOf<Pair<Int, Backend>>()      // (finish tick, backend)
        var last = 0
        for (t in 0 until 30) {
            running.filter { it.first <= t }.forEach { lb.finish(it.second) }
            running.removeAll { it.first <= t }
            val b = lb.pick()!!
            val end = maxOf(t, busyUntil.getValue(b.name)) + cost.getValue(b.name)
            busyUntil[b.name] = end
            running += end to b
            last = maxOf(last, end)
        }
        return "$last | " + bs.joinToString(" ") { "${it.name}=${it.served}" }
    }

    @Test fun `round-robin takes each backend in turn`() {
        assertEquals("b1 b2 b3 b1", Balancer(three(), Strategy.ROUND_ROBIN).names(4))
    }

    @Test fun `round-robin skips an unhealthy backend`() {
        val bs = three()
        bs[1].healthy = false
        assertEquals("b1 b3 b1 b3", Balancer(bs, Strategy.ROUND_ROBIN).names(4))
    }

    @Test fun `a pick counts the request, and finish ends it`() {
        val bs = three()
        val lb = Balancer(bs, Strategy.ROUND_ROBIN)
        val b = lb.pick()!!
        assertEquals("active after pick", 1, b.active)
        lb.finish(b)
        assertEquals("active after finish", 0, b.active)
        assertEquals("served after finish", 1, b.served)
    }

    @Test fun `least-connections picks the fewest in flight, ties to the earliest`() {
        val bs = three()
        val lb = Balancer(bs, Strategy.LEAST_CONNECTIONS)
        assertEquals("all idle: one each, in list order", "b1 b2 b3", lb.names(3))
        lb.finish(bs[0])
        lb.finish(bs[2])                     // now b1=0, b2=1, b3=0 in flight
        assertEquals("b1 b3 b1", lb.names(3))
    }

    @Test fun `no healthy backend gives null`() {
        val bs = three()
        bs.forEach { it.healthy = false }
        assertNull(Balancer(bs, Strategy.LEAST_CONNECTIONS).pick())
    }

    @Test fun `with one slow backend, least-connections finishes far sooner`() {
        assertEquals("round-robin", "201 | b1=10 b2=10 b3=10", simulate(Strategy.ROUND_ROBIN))
        assertEquals("least-connections", "41 | b1=15 b2=2 b3=13", simulate(Strategy.LEAST_CONNECTIONS))
    }
}
