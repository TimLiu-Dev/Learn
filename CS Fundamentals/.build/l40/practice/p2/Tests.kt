import org.junit.Assert.assertEquals
import org.junit.Test

class AutosquashTest {
    private fun commits(vararg subjects: String) = subjects.mapIndexed { i, s -> Commit("c$i", s) }

    @Test fun `ordinary commits are picked in order`() {
        assertEquals(listOf("pick c0 # Add total", "pick c1 # Add discount"),
            autosquash(commits("Add total", "Add discount")))
    }

    @Test fun `a fixup moves right after its target`() {
        assertEquals(listOf("pick c0 # Add discount", "fixup c3 # fixup! Add discount", "pick c1 # Add total", "pick c2 # Document units"),
            autosquash(commits("Add discount", "Add total", "Document units", "fixup! Add discount")))
    }

    @Test fun `squash! gets the squash action`() {
        assertEquals(listOf("pick c0 # Add total", "squash c2 # squash! Add total", "pick c1 # Add discount"),
            autosquash(commits("Add total", "Add discount", "squash! Add total")))
    }

    @Test fun `two fixups for one commit keep their order`() {
        assertEquals(listOf("pick c0 # Add discount", "fixup c2 # fixup! Add discount", "fixup c3 # fixup! Add discount", "pick c1 # Add total"),
            autosquash(commits("Add discount", "Add total", "fixup! Add discount", "fixup! Add discount")))
    }

    @Test fun `a fixup with no target on the branch stays a pick`() {
        assertEquals(listOf("pick c0 # Add total", "pick c1 # fixup! Add tax"),
            autosquash(commits("Add total", "fixup! Add tax")))
    }
}
