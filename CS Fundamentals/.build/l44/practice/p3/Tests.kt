import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RetryReviewTest {
    private val shortener = listOf(
        Endpoint("POST", "/api/links"),
        Endpoint("GET", "/{code}"),
        Endpoint("GET", "/api/links/{code}/stats"),
    )

    @Test fun `PUT, DELETE and the safe methods are idempotent`() {
        for (m in listOf("GET", "HEAD", "OPTIONS", "TRACE", "PUT", "DELETE"))
            assertTrue("$m should be idempotent", isIdempotent(m))
    }

    @Test fun `POST and PATCH are not`() {
        assertFalse("POST", isIdempotent("POST"))
        assertFalse("PATCH", isIdempotent("PATCH"))
    }

    @Test fun `the shortener without a key gets one comment`() {
        assertEquals(listOf("POST /api/links needs an Idempotency-Key"), retryProblems(shortener))
    }

    @Test fun `an Idempotency-Key answers the comment`() {
        val fixed = listOf(Endpoint("POST", "/api/links", idempotencyKey = true)) + shortener.drop(1)
        assertEquals(emptyList<String>(), retryProblems(fixed))
    }

    @Test fun `comments keep the API's order, and PATCH counts`() {
        val api = listOf(
            Endpoint("PATCH", "/api/links/{code}"),
            Endpoint("DELETE", "/api/links/{code}"),
            Endpoint("POST", "/api/pastes"),
        )
        assertEquals(listOf("PATCH /api/links/{code} needs an Idempotency-Key",
                            "POST /api/pastes needs an Idempotency-Key"), retryProblems(api))
    }

    @Test fun `an unknown method is not assumed safe`() {
        assertEquals(listOf("PURGE /cache needs an Idempotency-Key"), retryProblems(listOf(Endpoint("PURGE", "/cache"))))
    }
}
