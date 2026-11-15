import java.security.MessageDigest
import java.util.Base64

// PKCE S256 (RFC 7636 §4.2): BASE64URL-ENCODE(SHA256(ASCII(code_verifier))), without padding.
fun challengeOf(verifier: String): String =
    Base64.getUrlEncoder().withoutPadding()
        .encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)))

class CodeStore(private val now: () -> Long) {                 // seconds, injected so tests can move time
    private class Grant(val clientId: String, val redirectUri: String, val challenge: String, val issued: Long)
    private val grants = HashMap<String, Grant>()

    // /authorize calls this after ana clicks Allow.
    fun issue(code: String, clientId: String, redirectUri: String, challenge: String) {
        grants[code] = Grant(clientId, redirectUri, challenge, now())
    }

    fun redeem(code: String, clientId: String, redirectUri: String, verifier: String): Boolean {
        // remove() finds and retires the code in one step: unknown and already-used codes both give null.
        val g = grants.remove(code) ?: return false
        return now() - g.issued <= 60 &&
            g.clientId == clientId &&
            g.redirectUri == redirectUri &&                       // exact string match, never startsWith
            challengeOf(verifier) == g.challenge                  // only the app that started the flow knows it
    }
}
