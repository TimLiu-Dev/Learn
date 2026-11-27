import org.junit.Assert.*
import org.junit.Test

class WebhookSignatureTest {
    private val secret = "whsec-for-tests-only-0123456789ab".toByteArray()
    private val otherSecret = "another-secret-nobody-should-use!".toByteArray()
    private val t = 1_767_225_600L                      // 2026-01-01T00:00:00Z
    private val body = """{"id":"evt_1","type":"payment.succeeded","amount":4200}"""

    @Test fun `a genuine, recent delivery verifies`() {
        assertTrue(verify(secret, sign(secret, t, body), body, now = t + 10))
    }

    @Test fun `the header is t, then v1 with 64 hex digits`() {
        val header = sign(secret, t, body)
        assertTrue("got $header", Regex("t=1767225600,v1=[0-9a-f]{64}").matches(header))
    }

    @Test fun `an edited body is rejected`() {
        val forged = body.replace("4200", "999999")
        assertFalse(verify(secret, sign(secret, t, body), forged, now = t + 10))
    }

    @Test fun `a delivery signed with another secret is rejected`() {
        assertFalse(verify(secret, sign(otherSecret, t, body), body, now = t + 10))
    }

    @Test fun `a genuine delivery replayed six minutes later is rejected`() {
        assertFalse("301 s old", verify(secret, sign(secret, t, body), body, now = t + 301))
        assertTrue("300 s old is still inside the tolerance", verify(secret, sign(secret, t, body), body, now = t + 300))
    }

    @Test fun `a replay with a refreshed timestamp is rejected`() {
        val later = t + 3600
        val old = sign(secret, t, body)
        val refreshed = old.replace("t=$t", "t=$later")   // the attacker can't recompute the tag
        assertFalse(verify(secret, refreshed, body, now = later))
    }

    @Test fun `malformed headers give false, not an exception`() {
        for (h in listOf("", "v1=abc", "t=abc,v1=00", "t=1767225600", "t=1767225600,v1=not-hex"))
            assertFalse("header: '$h'", verify(secret, h, body, now = t))
    }
}
