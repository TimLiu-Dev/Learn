package runner

import kotlin.test.Test
import kotlin.test.assertEquals

class JobsFileTest {
    // Fails until Milestone 1: make it pass, then add tests for comments, blank lines and every error message.
    @Test
    fun `parses every field and fills in the defaults`() {
        val jobs = JobsFile.parse("id=a duration=200 fail=2 deps=b,c\nid=b duration=10\nid=c duration=0 deps=")
        assertEquals(Job("a", 200, 2, listOf("b", "c")), jobs[0])
        assertEquals(Job("b", 10, 0, emptyList()), jobs[1])
        assertEquals(Job("c", 0, 0, emptyList()), jobs[2])
    }
}
