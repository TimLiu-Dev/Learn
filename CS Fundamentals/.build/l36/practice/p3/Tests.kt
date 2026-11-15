import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class UpdateMemberTest {
    private fun rejected(changes: Map<String, Any?>): String {
        try {
            val q = updateMember(7, changes)
            fail("expected IllegalArgumentException, but got $q")
        } catch (e: IllegalArgumentException) {
            return e.message ?: ""
        }
        return ""
    }

    @Test fun `one field gives one placeholder`() {
        assertEquals(
            Query("UPDATE members SET email = ? WHERE id = ?", listOf("ada@example.org", 7)),
            updateMember(7, mapOf("email" to "ada@example.org")))
    }

    @Test fun `fields keep the caller's order and the id comes last`() {
        assertEquals(
            Query("UPDATE members SET name = ?, email = ? WHERE id = ?", listOf("Ada", "ada@example.org", 7)),
            updateMember(7, mapOf("name" to "Ada", "email" to "ada@example.org")))
    }

    @Test fun `a quote in a value never reaches the SQL text`() {
        val evil = "x' OR '1'='1"
        val q = updateMember(7, mapOf("name" to evil))
        assertFalse("the SQL text must not contain the user's input: ${q.sql}", q.sql.contains("OR"))
        assertFalse("the SQL text needs no quotes at all: ${q.sql}", q.sql.contains("'"))
        assertEquals("the value travels as a parameter", listOf(evil, 7), q.params)
    }

    @Test fun `a column outside the allowlist is rejected`() {
        rejected(mapOf("is_admin" to 1))
        val msg = rejected(mapOf("name = 'x', email" to "y"))
        assertTrue("say which key was refused, got: $msg", msg.contains("name = 'x', email"))
    }

    @Test fun `null is a value too`() {
        assertEquals(
            Query("UPDATE members SET email = ? WHERE id = ?", listOf(null, 7)),
            updateMember(7, mapOf("email" to null)))
    }

    @Test fun `an empty change is refused`() {
        rejected(emptyMap())
    }
}
