import org.junit.Assert.*
import org.junit.Test

class BindTest {
    @Test fun `env names follow relaxed binding`() {
        assertEquals("SHELF_POOLSIZE", envName("shelf.pool-size"))
        assertEquals("SPRING_MAIN_LOGSTARTUPINFO", envName("spring.main.log-startup-info"))
    }

    @Test fun `defaults apply when nothing is set`() {
        assertEquals(ShelfConfig("shelf.db", 8080, 4), bind(emptyMap(), emptyMap()))
    }

    @Test fun `properties are read and converted`() {
        val props = mapOf("shelf.db" to "data.db", "shelf.port" to "9000", "shelf.pool-size" to "8")
        assertEquals(ShelfConfig("data.db", 9000, 8), bind(props, emptyMap()))
    }

    @Test fun `an environment variable beats the properties file`() {
        val props = mapOf("shelf.port" to "9000")
        val env = mapOf("SHELF_PORT" to "7000", "SHELF_POOLSIZE" to "2", "PATH" to "/usr/bin")
        assertEquals(ShelfConfig("shelf.db", 7000, 2), bind(props, env))
    }

    @Test fun `port 0 is allowed, for a random port in tests`() {
        assertEquals(0, bind(mapOf("shelf.port" to "0"), emptyMap()).port)
    }

    @Test fun `every problem is reported at once`() {
        val props = mapOf("shelf.port" to "abc", "shelf.pool-size" to "0")
        val e = assertThrows(IllegalArgumentException::class.java) { bind(props, emptyMap()) }
        assertEquals("shelf.port: 'abc' is not a number; shelf.pool-size: 0 is out of range 1..64", e.message)
    }
}
