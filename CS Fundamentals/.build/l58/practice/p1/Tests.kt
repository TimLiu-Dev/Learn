import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DependencyRuleTest {
    private val rings = mapOf("domain" to 0, "app" to 1, "sqlite" to 2, "web" to 2, "main" to 3)
    private val hexagonal = mapOf(
        "domain" to emptySet(),
        "app" to setOf("domain"),
        "sqlite" to setOf("domain"),
        "web" to setOf("app", "domain"),
        "main" to setOf("app", "sqlite", "web", "domain"),
    )

    private fun assertIsCycle(deps: Map<String, Set<String>>, cycle: List<String>?) {
        requireNotNull(cycle) { "expected a cycle, got null" }
        assertTrue("a cycle ends where it starts: $cycle", cycle.size >= 2 && cycle.first() == cycle.last())
        cycle.zipWithNext().forEach { (a, b) -> assertTrue("$a doesn't depend on $b in $cycle", b in deps[a].orEmpty()) }
    }

    @Test fun `dependencies that point inward break no rule`() {
        assertEquals(emptyList<String>(), violations(rings, hexagonal))
    }

    @Test fun `the domain reaching out to the database is caught`() {
        val broken = hexagonal + ("domain" to setOf("sqlite"))
        assertEquals(listOf("domain -> sqlite"), violations(rings, broken))
    }

    @Test fun `every outward arrow is listed, sorted`() {
        val broken = hexagonal + ("app" to setOf("domain", "web", "sqlite")) + ("sqlite" to setOf("main"))
        assertEquals(listOf("app -> sqlite", "app -> web", "sqlite -> main"), violations(rings, broken))
    }

    @Test fun `a clean graph has no cycle`() {
        assertNull(findCycle(hexagonal))
    }

    @Test fun `two contexts that use each other form a cycle`() {
        val deps = mapOf("app" to setOf("lending"), "lending" to setOf("catalogue"), "catalogue" to setOf("lending"))
        val cycle = findCycle(deps)
        assertIsCycle(deps, cycle)
        assertEquals(setOf("lending", "catalogue"), cycle!!.toSet())
    }

    @Test fun `a longer loop is found too`() {
        val deps = mapOf("a" to setOf("b"), "b" to setOf("c"), "c" to setOf("a"), "d" to setOf("a"))
        assertIsCycle(deps, findCycle(deps))
    }
}
