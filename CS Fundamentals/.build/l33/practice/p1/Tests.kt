import org.junit.Assert.*
import org.junit.Test

class HmacTokenTest {
    private val key = "k3y-for-tests-only-0123456789abc".toByteArray()
    private val otherKey = "another-key-nobody-should-trust!".toByteArray()

    @Test fun `a signed token verifies and gives back the message`() {
        assertEquals("user=ana", verify(key, sign(key, "user=ana")))
    }

    @Test fun `the token is the message, a dot and 64 hex digits`() {
        val token = sign(key, "hi")
        assertTrue("got $token", Regex("hi\\.[0-9a-f]{64}").matches(token))
    }

    @Test fun `an edited message is rejected`() {
        val edited = sign(key, "user=ana;role=user").replace("role=user", "role=admin")
        assertNull("an edited token must not verify", verify(key, edited))
    }

    @Test fun `a token signed with another key is rejected`() {
        assertNull(verify(key, sign(otherKey, "user=ana")))
    }

    @Test fun `a message may contain dots`() {
        assertEquals("v1.2.3", verify(key, sign(key, "v1.2.3")))
    }

    @Test fun `malformed tokens give null, not an exception`() {
        assertNull(verify(key, "no-dot-here"))
        assertNull(verify(key, "user=ana.not-hex"))
        assertNull(verify(key, ""))
    }
}
