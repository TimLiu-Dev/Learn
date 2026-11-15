import org.junit.Assert.assertEquals
import org.junit.Test

class ReadNoteTest {
    @Test fun `ana reads her own note`() {
        assertEquals(200 to "Ana's plan", readNote("tok-ana", 1))
    }

    @Test fun `ben cannot read ana's note`() {
        assertEquals(404 to "not found", readNote("tok-ben", 1))
    }

    @Test fun `someone else's note looks exactly like a missing one`() {
        assertEquals(readNote("tok-ben", 99), readNote("tok-ben", 1))
    }

    @Test fun `no token is 401`() {
        assertEquals(401 to "who are you?", readNote(null, 1))
    }

    @Test fun `an unknown token is 401`() {
        assertEquals(401 to "who are you?", readNote("tok-eve", 2))
    }

    @Test fun `a stranger can't probe which ids exist`() {
        assertEquals("missing and existing notes must answer the same", readNote(null, 1), readNote(null, 99))
    }
}
