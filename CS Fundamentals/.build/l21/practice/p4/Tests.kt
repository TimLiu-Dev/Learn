import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ScheduleTest {
    @Test fun `ready tasks run in alphabetical order`() {
        assertEquals(listOf("a", "b", "c"), schedule(mapOf("b" to listOf(), "c" to listOf(), "a" to listOf())))
    }

    @Test fun `a task unlocked later can jump the queue`() {
        val needs = mapOf("z" to listOf(), "b" to listOf(), "a" to listOf("b"))
        assertEquals("a is unlocked after z is ready, but a < z", listOf("b", "a", "z"), schedule(needs))
    }

    @Test fun `prerequisites always come first`() {
        val build = mapOf(
            "compile" to listOf(), "resources" to listOf(), "classes" to listOf("compile", "resources"),
            "compileTest" to listOf("classes"), "test" to listOf("compileTest"),
            "jar" to listOf("classes"), "build" to listOf("jar", "test"),
        )
        assertEquals(listOf("compile", "resources", "classes", "compileTest", "jar", "test", "build"), schedule(build))
    }

    @Test fun `a cycle gives null`() {
        assertNull(schedule(mapOf("a" to listOf("c"), "b" to listOf("a"), "c" to listOf("b"), "d" to listOf())))
    }

    @Test fun `no tasks gives an empty schedule`() {
        assertEquals(emptyList<String>(), schedule(emptyMap()))
    }
}
