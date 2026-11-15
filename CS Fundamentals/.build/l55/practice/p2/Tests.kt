import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TokenChecksTest {
    private val now = 1_790_000_300L
    private fun claims(vararg extra: Pair<String, Any?>) =
        mapOf<String, Any?>("iss" to "https://books.example", "sub" to "ana", "aud" to "books-api", "exp" to now + 600) + extra

    @Test fun `a genuine token passes both checks`() {
        assertNull("header", headerProblem(mapOf("alg" to "HS256", "typ" to "JWT")))
        assertNull("claims", claimsProblem(claims(), now))
    }

    @Test fun `only HS256 is accepted, whatever the token asks for`() {
        for (alg in listOf("none", "None", "HS512", "RS256", "hs256"))
            assertEquals("alg $alg", "alg must be HS256", headerProblem(mapOf("alg" to alg, "typ" to "JWT")))
        assertEquals("no alg at all", "alg must be HS256", headerProblem(mapOf("typ" to "JWT")))
    }

    @Test fun `exp is required, and must be a number`() {
        assertEquals("missing exp", "no exp", claimsProblem(claims() - "exp", now))
        assertEquals("exp as a string", "no exp", claimsProblem(claims("exp" to "9999999999"), now))
    }

    @Test fun `the leeway covers small clock skew, no more`() {
        assertNull("expired 30 s ago", claimsProblem(claims("exp" to now - 30), now))
        assertEquals("expired 60 s ago", "expired", claimsProblem(claims("exp" to now - 60), now))
        assertEquals("expired 10 min ago", "expired", claimsProblem(claims("exp" to now - 600), now))
    }

    @Test fun `a token for another service is rejected`() {
        assertEquals("billing-api", "wrong audience", claimsProblem(claims("aud" to "billing-api"), now))
        assertEquals("no aud", "wrong audience", claimsProblem(claims() - "aud", now))
    }
}
