package runner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlanTest {
    private fun ids(text: String) = Plan.order(JobsFile.parse(text)).map { it.id }

    @Test
    fun `starts with the jobs that need nothing, in file order`() {
        assertEquals(listOf("a", "c", "b"), ids("id=a duration=1\nid=b duration=1 deps=a\nid=c duration=1"))
    }

    @Test
    fun `moves dependencies before their dependents`() {
        assertEquals(listOf("fetch", "build", "report"),
            ids("id=report duration=1 deps=build\nid=build duration=1 deps=fetch\nid=fetch duration=1"))
    }

    @Test
    fun `reports a cycle as a path that closes on itself`() {
        val e = assertFailsWith<InputError> { ids("id=a duration=1 deps=c\nid=b duration=1 deps=a\nid=c duration=1 deps=b\nid=d duration=1") }
        assertEquals("dependency cycle: a -> c -> b -> a", e.message)
        assertEquals("dependency cycle: a -> a", assertFailsWith<InputError> { ids("id=a duration=1 deps=a") }.message)
    }

    @Test
    fun `backoff doubles from 50 ms`() {
        assertEquals(listOf(50L, 100L, 200L, 400L), (1..4).map(::backoffMs))
    }
}
