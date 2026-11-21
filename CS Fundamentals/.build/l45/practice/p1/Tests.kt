import org.junit.Assert.assertEquals
import org.junit.Test

class MatchTest {
    private val routes = listOf(
        Route("GET", "/books", "list"),
        Route("POST", "/books", "create"),
        Route("GET", "/books/{id}", "show"),
        Route("DELETE", "/books/{id}", "remove"),
        Route("POST", "/loans/{id}/return", "giveBack"),
        Route("GET", "/members/{member}/loans/{loan}", "memberLoan"),
    )

    @Test fun `a template binds its path parameters`() {
        assertEquals(Match.Found("giveBack", mapOf("id" to "42")), match(routes, "POST", "/loans/42/return"))
    }

    @Test fun `two parameters bind by name`() {
        assertEquals(Match.Found("memberLoan", mapOf("member" to "7", "loan" to "3")), match(routes, "GET", "/members/7/loans/3"))
    }

    @Test fun `empty segments do not matter`() {
        assertEquals(Match.Found("list", emptyMap()), match(routes, "GET", "/books/"))
        assertEquals(Match.Found("show", mapOf("id" to "5")), match(routes, "GET", "//books//5"))
    }

    @Test fun `a known path with the wrong method lists the allowed ones, sorted`() {
        assertEquals("PATCH /books/7", Match.WrongMethod(listOf("DELETE", "GET")), match(routes, "PATCH", "/books/7"))
        assertEquals("PUT /books", Match.WrongMethod(listOf("GET", "POST")), match(routes, "PUT", "/books"))
    }

    @Test fun `allowed methods are listed once each`() {
        val twoGets = listOf(Route("GET", "/books/{id}", "byId"), Route("GET", "/books/{isbn}", "byIsbn"))
        assertEquals(Match.WrongMethod(listOf("GET")), match(twoGets, "DELETE", "/books/7"))
    }

    @Test fun `an unknown path is no route at all`() {
        assertEquals("/books/7/pages", Match.NoRoute, match(routes, "GET", "/books/7/pages"))
        assertEquals("/authors", Match.NoRoute, match(routes, "GET", "/authors"))
        assertEquals("/loans/42/renew", Match.NoRoute, match(routes, "POST", "/loans/42/renew"))
    }
}
