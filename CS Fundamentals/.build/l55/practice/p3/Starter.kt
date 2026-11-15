import java.security.MessageDigest
import java.util.Base64

// PKCE S256 (RFC 7636 §4.2): BASE64URL-ENCODE(SHA256(ASCII(code_verifier))), without padding.
fun challengeOf(verifier: String): String =
    TODO("SHA-256 the ASCII bytes, then a base64url encoder without padding")

class CodeStore(private val now: () -> Long) {                 // seconds, injected so tests can move time
    private class Grant(val clientId: String, val redirectUri: String, val challenge: String, val issued: Long)
    private val grants = HashMap<String, Grant>()

    // /authorize calls this after ana clicks Allow.
    fun issue(code: String, clientId: String, redirectUri: String, challenge: String) {
        grants[code] = Grant(clientId, redirectUri, challenge, now())
    }

    // /token calls this. True only if every check passes; either way, the code is used up.
    fun redeem(code: String, clientId: String, redirectUri: String, verifier: String): Boolean =
        TODO("take the grant out of the map, then check age, client, redirect_uri and PKCE")
}
