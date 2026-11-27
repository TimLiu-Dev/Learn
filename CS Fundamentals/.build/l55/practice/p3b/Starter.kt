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
fun problem(a: Assertion, expectedChallenge: String, key: PublicKey): String? =
    TODO("check the client data, then the authenticator data, then the signature")
