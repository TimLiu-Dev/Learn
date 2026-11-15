import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CsrfGateTest {
    private val token = "q7Xn2_hR9vLk0pZs4TbWm1yCe8uJd3fGa6iOx5NrVQE"

    @Test fun `our own page's POST with the token is allowed`() {
        assertTrue(csrfAllowed(Request("POST", OUR_ORIGIN, token), token))
    }

    @Test fun `safe methods need neither Origin nor token`() {
        for (m in listOf("GET", "HEAD", "OPTIONS"))
            assertTrue("$m should pass", csrfAllowed(Request(m, null, null), token))
    }

    @Test fun `a foreign or lookalike Origin is rejected, even with the right token`() {
        for (o in listOf("https://evil.example", "https://books.example.evil.example", "http://books.example", "null", null))
            assertFalse("Origin $o should be rejected", csrfAllowed(Request("POST", o, token), token))
    }

    @Test fun `a missing, guessed or truncated token is rejected`() {
        for (t in listOf(null, "", "guess", token.dropLast(1), token + "A"))
            assertFalse("token \"$t\" should be rejected", csrfAllowed(Request("POST", OUR_ORIGIN, t), token))
    }

    @Test fun `PATCH and unknown methods count as unsafe`() {
        for (m in listOf("PATCH", "PUT", "DELETE", "PURGE"))
            assertFalse("$m without a token should be rejected", csrfAllowed(Request(m, OUR_ORIGIN, null), token))
    }

    @Test fun `tokensMatch compares whole values`() {
        assertTrue("same text, different String object", tokensMatch(token, String(token.toCharArray())))
        assertFalse("null", tokensMatch(token, null))
        assertFalse("different case", tokensMatch(token, token.uppercase()))
        assertFalse("only a prefix", tokensMatch(token, token.take(10)))
    }
}
