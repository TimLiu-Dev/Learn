package runner

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JobsFileTest {
    @Test
    fun `parses every field and fills in the defaults`() {
        val jobs = JobsFile.parse("id=a duration=200 fail=2 deps=b,c\nid=b duration=10\nid=c duration=0 deps=")
        assertEquals(Job("a", 200, 2, listOf("b", "c")), jobs[0])
        assertEquals(Job("b", 10, 0, emptyList()), jobs[1])
        assertEquals(Job("c", 0, 0, emptyList()), jobs[2])
    }

    @Test
    fun `skips comments and blank lines but counts them in line numbers`() {
        val e = assertFailsWith<InputError> { JobsFile.parse("# header\n\nid=a duration=1\nid=b duration=x") }
        assertEquals("line 4: duration must be a whole number ≥ 0, got 'x'", e.message)
    }

    @Test
    fun `names the line and the problem`() {
        assertEquals("line 2: unknown key 'colour'",
            assertFailsWith<InputError> { JobsFile.parse("id=a duration=1\nid=b duration=1 colour=red") }.message)
        assertEquals("line 2: duplicate id 'a' (first on line 1)",
            assertFailsWith<InputError> { JobsFile.parse("id=a duration=1\nid=a duration=2") }.message)
        assertEquals("line 1: unknown dependency 'ghost' in job 'a'",
            assertFailsWith<InputError> { JobsFile.parse("id=a duration=1 deps=ghost") }.message)
        assertEquals("line 1: missing duration",
            assertFailsWith<InputError> { JobsFile.parse("id=a") }.message)
    }
}
