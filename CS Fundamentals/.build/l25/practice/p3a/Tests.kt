import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskGraphTest {
    private val build = mapOf(
        "compileKotlin" to listOf(),
        "processResources" to listOf(),
        "classes" to listOf("compileKotlin", "processResources"),
        "jar" to listOf("classes"),
        "test" to listOf("classes"),
        "assemble" to listOf("jar"),
        "check" to listOf("test"),
        "build" to listOf("assemble", "check"),
        "javadoc" to listOf("classes"),
    )

    @Test fun `dependencies run first`() {
        assertEquals(listOf("compileKotlin", "processResources", "classes", "jar"), executionOrder(build, listOf("jar")))
    }

    @Test fun `a shared dependency runs once`() {
        assertEquals(
            listOf("compileKotlin", "processResources", "classes", "jar", "assemble", "test", "check", "build"),
            executionOrder(build, listOf("build")))
    }

    @Test fun `two requested tasks share their work`() {
        assertEquals(listOf("compileKotlin", "processResources", "classes", "test", "jar"), executionOrder(build, listOf("test", "jar")))
    }

    @Test fun `tasks nobody needs do not run`() {
        assertTrue("javadoc was not requested", "javadoc" !in executionOrder(build, listOf("build")))
    }

    @Test fun `an unknown task is named`() {
        val e = assertThrows(IllegalArgumentException::class.java) { executionOrder(build + ("deploy" to listOf("pubish")), listOf("deploy")) }
        assertTrue("message: ${e.message}", e.message!!.contains("pubish"))
    }

    @Test fun `a cycle is reported with its path`() {
        val looped = mapOf("a" to listOf("b"), "b" to listOf("a"))
        val e = assertThrows(IllegalStateException::class.java) { executionOrder(looped, listOf("a")) }
        assertTrue("message: ${e.message}", e.message!!.contains("a -> b -> a"))
    }
}
