import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TokenEndpointTest {
    // RFC 7636 Appendix B's verifier and challenge
    private val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
    private val challenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
    private val app = "photo-app"
    private val callback = "https://photo.example/callback"
    private var clock = 1_790_000_000L
    private val store = CodeStore { clock }
    private fun issued(code: String) = code.also { store.issue(it, app, callback, challenge) }

    @Test fun `challengeOf matches RFC 7636 Appendix B`() {
        assertEquals(challenge, challengeOf(verifier))
    }

    @Test fun `the app that started the flow can redeem its code`() {
        assertTrue(store.redeem(issued("c1"), app, callback, verifier))
    }

    @Test fun `a copied code without the verifier is useless`() {
        assertFalse("a made-up verifier", store.redeem(issued("c1"), app, callback, "x".repeat(43)))
        assertFalse("the challenge seen in the URL", store.redeem(issued("c2"), app, callback, challenge))
        assertFalse("a code that was never issued", store.redeem("c3", app, callback, verifier))
    }

    @Test fun `a code works once, and a failed attempt uses it up too`() {
        val c = issued("c1")
        assertTrue("first redeem", store.redeem(c, app, callback, verifier))
        assertFalse("second redeem", store.redeem(c, app, callback, verifier))
        val d = issued("c2")
        store.redeem(d, app, callback, "x".repeat(43))
        assertFalse("the right verifier after a failed attempt", store.redeem(d, app, callback, verifier))
    }

    @Test fun `the code is bound to its client and its exact redirect_uri`() {
        assertFalse("another client", store.redeem(issued("c1"), "ad-app", callback, verifier))
        assertFalse("a longer redirect_uri", store.redeem(issued("c2"), app, "$callback/../open?to=evil", verifier))
        assertFalse("another host", store.redeem(issued("c3"), app, "https://evil.example/callback", verifier))
    }

    @Test fun `a code older than 60 seconds is rejected`() {
        val c = issued("c1")
        clock += 60
        assertTrue("60 s old", store.redeem(c, app, callback, verifier))
        val d = issued("c2")
        clock += 61
        assertFalse("61 s old", store.redeem(d, app, callback, verifier))
    }
}
