import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionTest {
    @Test fun `versions compare numerically, not as text`() {
        assertTrue("1.10.0 should be newer than 1.9.0", compareVersions("1.10.0", "1.9.0") > 0)
        assertTrue("2.0.0 should be older than 10.0.0", compareVersions("2.0.0", "10.0.0") < 0)
        assertEquals("equal versions", 0, compareVersions("3.1.4", "3.1.4"))
    }

    @Test fun `the highest requested version wins`() {
        val requests = listOf(
            "org.junit.jupiter:junit-jupiter-engine:5.10.1",
            "org.junit.jupiter:junit-jupiter-engine:6.0.1",
        )
        assertEquals(mapOf("org.junit.jupiter:junit-jupiter-engine" to "6.0.1"), resolve(requests))
    }

    @Test fun `each module is resolved on its own`() {
        val requests = listOf(
            "com.example:json:1.9.0",
            "org.slf4j:slf4j-api:2.0.17",
            "com.example:json:1.10.0",
            "com.example:json:1.2.3",
        )
        assertEquals(
            mapOf("com.example:json" to "1.10.0", "org.slf4j:slf4j-api" to "2.0.17"),
            resolve(requests),
        )
    }

    @Test fun `only a major bump is breaking`() {
        assertTrue("2.4.1 -> 3.0.0", isBreaking("2.4.1", "3.0.0"))
        assertFalse("2.4.1 -> 2.5.0 adds features only", isBreaking("2.4.1", "2.5.0"))
        assertFalse("2.4.1 -> 2.4.2 fixes bugs only", isBreaking("2.4.1", "2.4.2"))
    }

    @Test fun `before version 1 anything may break`() {
        assertTrue("0.3.1 -> 0.4.0", isBreaking("0.3.1", "0.4.0"))
        assertTrue("0.3.1 -> 0.3.2", isBreaking("0.3.1", "0.3.2"))
    }
}
