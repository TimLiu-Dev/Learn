import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

// PKCE (RFC 7636): the app keeps a random secret (the verifier) and sends only its hash (the challenge).
val b64 = Base64.getUrlEncoder().withoutPadding()

fun newVerifier(): String = b64.encodeToString(ByteArray(32).also { SecureRandom().nextBytes(it) })   // §4.1
fun challengeOf(verifier: String): String =                                                             // §4.2, S256
    b64.encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII)))

fun main() {
    // RFC 7636 Appendix B: these 32 octets...
    val octets = intArrayOf(116, 24, 223, 180, 151, 153, 224, 37, 79, 250, 96, 125, 216, 173, 187, 186,
        22, 212, 37, 77, 105, 214, 191, 240, 91, 88, 5, 88, 83, 132, 141, 121).map { it.toByte() }.toByteArray()
    val verifier = b64.encodeToString(octets)
    println("verifier:  $verifier")
    println("challenge: ${challengeOf(verifier)}")
    println("both match Appendix B: ${verifier == "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk" &&
        challengeOf(verifier) == "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"}")

    val mine = newVerifier()
    println("a fresh verifier has ${mine.length} chars, all unreserved: ${mine.all { it.isLetterOrDigit() || it in "-._~" }}")
    println("its challenge: ${challengeOf(mine)}")
}
