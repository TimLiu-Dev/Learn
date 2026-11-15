import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DnsCacheTest {
    private val example = listOf("104.20.23.154", "172.66.147.243")
    private var now = 1_000L
    private val cache = DnsCache({ now })

    @Test fun `a fresh answer has its full TTL`() {
        cache.store("example.com", example, 300)
        assertEquals(Hit(example, 300), cache.lookup("example.com"))
    }

    @Test fun `the TTL counts down like dig`() {
        cache.store("example.com", example, 169)
        now += 7
        assertEquals(Hit(example, 162), cache.lookup("example.com"))
    }

    @Test fun `an answer is gone when its TTL runs out`() {
        cache.store("example.com", example, 300)
        now += 299
        assertEquals("one second before the end", Hit(example, 1), cache.lookup("example.com"))
        now += 1
        assertNull("at 0 seconds left it must be discarded", cache.lookup("example.com"))
    }

    @Test fun `an unknown name is a miss`() {
        cache.store("example.com", example, 300)
        assertNull(cache.lookup("example.org"))
    }

    @Test fun `storing again restarts the countdown`() {
        cache.store("example.com", example, 300)
        now += 200
        cache.store("example.com", listOf("172.66.147.243"), 300)
        assertEquals(Hit(listOf("172.66.147.243"), 300), cache.lookup("example.com"))
    }

    @Test fun `a JVM-style cache ignores the record's TTL`() {
        val jvm = DnsCache({ now }, fixedTtl = 30)
        jvm.store("db.internal", listOf("10.0.0.5"), 5)
        jvm.store("example.com", example, 3600)
        now += 20
        assertEquals("a 5 s record is still served after 20 s", Hit(listOf("10.0.0.5"), 10), jvm.lookup("db.internal"))
        now += 10
        assertNull("a 3600 s record is dropped after 30 s", jvm.lookup("example.com"))
    }
}
