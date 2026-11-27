import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class MigrateTest {
    private val v1 = Migration(1, "create books")
    private val v2 = Migration(2, "create members\ncreate loans")
    private val v3 = Migration(3, "drop loans")

    @Test fun `a fresh database gets every migration in version order`() {
        val db = FakeDatabase()
        assertEquals(listOf(1, 2, 3), migrate(db, listOf(v3, v1, v2)))
        assertEquals(setOf("books", "members"), db.tables)
        assertEquals(setOf(1, 2, 3), db.history.keys)
    }

    @Test fun `running it again applies nothing`() {
        val db = FakeDatabase()
        migrate(db, listOf(v1, v2))
        assertEquals("second run", emptyList<Int>(), migrate(db, listOf(v1, v2)))
    }

    @Test fun `a new migration is the only one applied`() {
        val db = FakeDatabase()
        migrate(db, listOf(v1, v2))
        assertEquals(listOf(3), migrate(db, listOf(v1, v2, v3)))
    }

    @Test fun `an edited migration is refused before anything runs`() {
        val db = FakeDatabase()
        migrate(db, listOf(v1))
        try {
            migrate(db, listOf(Migration(1, "create bookz"), v2))
            fail("expected an IllegalStateException")
        } catch (e: IllegalStateException) {
            assertTrue("message should name V1, was: ${e.message}", e.message!!.contains("V1"))
        }
        assertEquals("V2 must not run after a refusal", setOf("books"), db.tables)
    }

    @Test fun `a failing migration leaves no trace but earlier ones stay`() {
        val db = FakeDatabase()
        val broken = Migration(2, "create members\nalter members")
        try { migrate(db, listOf(v1, broken)); fail("expected the syntax error to escape") } catch (e: IllegalStateException) { }
        assertEquals(setOf("books"), db.tables)
        assertEquals("history", setOf(1), db.history.keys)
    }
}
