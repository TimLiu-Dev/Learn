package shelf.http

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RouterTest {
    private val ok: Handler = { Response(200) }
    private val router = Router().apply {
        get("/books", ok)
        post("/books", ok)
        get("/books/{id}", ok)
        delete("/books/{id}", ok)
        post("/loans/{id}/return", ok)
    }

    @Test
    fun `a template binds its path parameters`() {
        val m = assertIs<Match.Found>(router.match("POST", "/loans/42/return"))
        assertEquals(mapOf("id" to "42"), m.params)
    }

    @Test
    fun `trailing slashes do not matter`() {
        assertIs<Match.Found>(router.match("GET", "/books/"))
    }

    @Test
    fun `a known path with the wrong method lists the allowed ones`() {
        val m = assertIs<Match.WrongMethod>(router.match("PATCH", "/books/7"))
        assertEquals(listOf("DELETE", "GET"), m.allowed)
    }

    @Test
    fun `an unknown path is no route at all`() {
        assertEquals(Match.NoRoute, router.match("GET", "/books/7/pages"))
        assertEquals(Match.NoRoute, router.match("GET", "/authors"))
    }
}
