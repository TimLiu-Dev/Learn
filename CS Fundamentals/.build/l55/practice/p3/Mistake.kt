import java.security.MessageDigest
import java.util.Base64

fun challengeOf(verifier: String): String =
    Base64.getUrlEncoder().withoutPadding()
        .encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)))

class CodeStore(private val now: () -> Long) {
    private class Grant(val clientId: String, val redirectUri: String, val challenge: String, val issued: Long)
    private val grants = HashMap<String, Grant>()

    fun issue(code: String, clientId: String, redirectUri: String, challenge: String) {
        grants[code] = Grant(clientId, redirectUri, challenge, now())
    }

    // Looks the code up but never retires it.
    fun redeem(code: String, clientId: String, redirectUri: String, verifier: String): Boolean {
        val g = grants[code] ?: return false
        return now() - g.issued <= 60 && g.clientId == clientId && g.redirectUri == redirectUri &&
            challengeOf(verifier) == g.challenge
    }
}
