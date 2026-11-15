import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

val hotLog = listOf(
    "10    8 %     3       Hot::work @ 4 (29 bytes)",
    "10    9       3       Hot::work (29 bytes)",
    "10   10 %     4       Hot::work @ 4 (29 bytes)",
    "10    8 %     3       Hot::work @ 4 (29 bytes)   made not entrant: OSR invalidation of lower level",
    "19  105       4       Hot::work (29 bytes)",
    "20    9       3       Hot::work (29 bytes)   made not entrant: not used",
)

class PrintCompilationTest {
    @Test fun `an OSR compile at tier 4`() {
        assertEquals(Compile(10, 10, true, 4, "Hot::work", 29, false), parse(hotLog[2]))
    }

    @Test fun `a full-method compile is not OSR`() {
        assertEquals(Compile(19, 105, false, 4, "Hot::work", 29, false), parse(hotLog[4]))
    }

    @Test fun `made not entrant marks retired code`() {
        assertEquals(Compile(20, 9, false, 3, "Hot::work", 29, true), parse(hotLog[5]))
    }

    @Test fun `no tier column when tiered compilation is off`() {
        assertEquals(Compile(10, 4, true, null, "Hot::work", 29, false), parse("10    4 %           Hot::work @ 4 (29 bytes)"))
    }

    @Test fun `C2's full-method compile landed at 19 ms, not the OSR one at 10`() {
        assertEquals(19, c2Landed(hotLog, "Hot::work"))
    }

    @Test fun `the final exercise's log, and a method that never reached C2`() {
        val hunt = listOf(
            "17   79       3       HotHunt::collatzSteps (42 bytes)",
            "17   80 %     4       HotHunt::collatzSteps @ 2 (42 bytes)",
            "23  141       4       HotHunt::collatzSteps (42 bytes)",
            "23   79       3       HotHunt::collatzSteps (42 bytes)   made not entrant: not used",
        )
        assertEquals(23, c2Landed(hunt, "HotHunt::collatzSteps"))
        assertNull("Hot::work isn't in this log", c2Landed(hunt, "Hot::work"))
    }
}
