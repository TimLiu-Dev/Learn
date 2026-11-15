import org.junit.Assert.assertEquals
import org.junit.Test

class NotesApiTest {
    @Test fun `a new note is 201 Created with its Location`() {
        val r = NotesApi().handle("POST", "/notes", "milk")
        assertEquals(201, r.status)
        assertEquals("/notes/2", r.headers["Location"])
    }

    @Test fun `a blank title is 422, well-formed but invalid`() {
        assertEquals(422, NotesApi().handle("POST", "/notes", "   ").status)
    }

    @Test fun `a duplicate title is 409 Conflict`() {
        assertEquals(409, NotesApi().handle("POST", "/notes", "groceries").status)
    }

    @Test fun `a known path with the wrong method is 405 with Allow`() {
        val api = NotesApi()
        val list = api.handle("DELETE", "/notes")
        assertEquals("DELETE /notes", 405, list.status)
        assertEquals("Allow on /notes", "GET, POST", list.headers["Allow"])
        val one = api.handle("PUT", "/notes/1", "eggs")
        assertEquals("PUT /notes/1", 405, one.status)
        assertEquals("Allow on /notes/1", "GET", one.headers["Allow"])
    }

    @Test fun `a missing note or an unknown path is 404`() {
        val api = NotesApi()
        for (path in listOf("/notes/7", "/notes/0", "/notes/abc", "/users")) {
            assertEquals("GET $path", 404, api.handle("GET", path).status)
        }
    }

    @Test fun `GET lists the notes and fetches one`() {
        val api = NotesApi()
        api.handle("POST", "/notes", "milk")
        assertEquals("groceries\nmilk", api.handle("GET", "/notes").body)
        assertEquals("milk", api.handle("GET", "/notes/2").body)
    }
}
