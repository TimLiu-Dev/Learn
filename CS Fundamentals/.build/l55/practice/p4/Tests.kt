import org.junit.Assert.assertEquals
import org.junit.Test

class DecideTest {
    private val ana = User("ana")
    private val ben = User("ben")
    private val root = User("root", admin = true)
    private val diary = Note(owner = "ana")                    // private
    private val recipe = Note(owner = "ana", isPublic = true)

    @Test fun `anyone may read a public note, and a missing one is 404`() {
        assertEquals("anonymous", 200, decide(null, Action.READ, recipe))
        assertEquals("ben", 200, decide(ben, Action.READ, recipe))
        assertEquals("no such note", 404, decide(ana, Action.READ, null))
    }

    @Test fun `no login is 401 for everything else`() {
        assertEquals("read a private note", 401, decide(null, Action.READ, diary))
        assertEquals("edit a public note", 401, decide(null, Action.EDIT, recipe))
    }

    @Test fun `the owner may read, edit and delete`() {
        for (a in listOf(Action.READ, Action.EDIT, Action.DELETE))
            assertEquals("ana $a", 200, decide(ana, a, diary))
    }

    @Test fun `another user is refused with 403`() {
        for (a in listOf(Action.READ, Action.EDIT, Action.DELETE))
            assertEquals("ben $a on ana's diary", 403, decide(ben, a, diary))
        assertEquals("ben EDIT on ana's public recipe", 403, decide(ben, Action.EDIT, recipe))
    }

    @Test fun `an admin may read and delete, but not edit`() {
        assertEquals("READ", 200, decide(root, Action.READ, diary))
        assertEquals("DELETE", 200, decide(root, Action.DELETE, diary))
        assertEquals("EDIT", 403, decide(root, Action.EDIT, diary))
    }

    @Test fun `an action with no rule is denied to everyone`() {
        assertEquals("owner", 403, decide(ana, Action.SHARE, diary))
        assertEquals("admin", 403, decide(root, Action.SHARE, diary))
    }
}
