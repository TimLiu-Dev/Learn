import java.security.MessageDigest
import java.security.PublicKey
import java.security.Signature

const val ORIGIN = "https://books.example"   // the only page allowed to log in
const val RP_ID = "books.example"            // the passkey's scope

class Assertion(val clientDataJSON: ByteArray, val authenticatorData: ByteArray, val signature: ByteArray)

fun sha256(b: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(b)

// Given: reads one string field from flat JSON like {"type":"webauthn.get","challenge":"…","origin":"…"}
fun field(json: ByteArray, name: String): String? =
    Regex("\"$name\"\\s*:\\s*\"([^\"]*)\"").find(json.decodeToString())?.groupValues?.get(1)

// null means accept. Order: type, challenge, origin, RP ID hash, user-present flag, signature.
fun problem(a: Assertion, expectedChallenge: String, key: PublicKey): String? {
    val cd = a.clientDataJSON
    if (field(cd, "type") != "webauthn.get") return "wrong type"
    if (field(cd, "challenge") != expectedChallenge) return "wrong challenge"
    if (field(cd, "origin") != ORIGIN) return "wrong origin"              // exact match, never endsWith
    val ad = a.authenticatorData
    if (!ad.copyOfRange(0, 32).contentEquals(sha256(RP_ID.toByteArray()))) return "wrong RP ID"
    if (ad[32].toInt() and 0x01 == 0) return "user not present"
    val ok = Signature.getInstance("Ed25519").run {
        initVerify(key); update(ad + sha256(cd)); verify(a.signature)
    }
    return if (ok) null else "bad signature"
}
