import java.nio.ByteBuffer
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

// A simplified WebAuthn: the real data formats (CBOR, COSE keys, attestation) are left out; the signed bytes are the real shape.
val b64 = Base64.getUrlEncoder().withoutPadding()
fun sha256(b: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(b)

class Assertion(val credentialId: String, val clientDataJSON: ByteArray, val authenticatorData: ByteArray, val signature: ByteArray)

// The authenticator (the phone's password manager, a security key): private keys never leave it.
class Authenticator {
    private val keys = HashMap<String, Pair<String, PrivateKey>>()     // credential id -> (RP ID, private key)
    private var signCount = 0
    fun register(rpId: String): Pair<String, ByteArray> {
        val pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()   // COSE alg -8, L33's signature
        val id = b64.encodeToString(ByteArray(16).also { SecureRandom().nextBytes(it) })
        keys[id] = rpId to pair.private
        return id to pair.public.encoded                               // the server gets only the public half
    }
    // authenticatorData = SHA-256(RP ID) ‖ flags ‖ signCount; the signature covers it and SHA-256(clientDataJSON).
    fun get(id: String, rpId: String, clientDataJSON: ByteArray): Assertion {
        val authData = ByteBuffer.allocate(37).put(sha256(rpId.toByteArray())).put(0x05).putInt(++signCount).array()  // flags: UP + UV
        val sig = Signature.getInstance("Ed25519").run { initSign(keys.getValue(id).second); update(authData + sha256(clientDataJSON)); sign() }
        return Assertion(id, clientDataJSON, authData, sig)
    }
}

// The browser writes clientDataJSON itself, with the origin of the page that asked. The page can't choose it.
fun clientData(challenge: String, origin: String) =
    buildJsonObject { put("type", "webauthn.get"); put("challenge", challenge); put("origin", origin) }.toString().toByteArray()

// The relying party (our server): stores public keys and one-time challenges, never a secret.
class RelyingParty(private val rpId: String, private val origin: String) {
    private val publicKeys = HashMap<String, ByteArray>()            // credential id -> public key
    private val challenges = HashSet<String>()
    fun store(id: String, publicKey: ByteArray) { publicKeys[id] = publicKey }
    fun challenge(): String = b64.encodeToString(ByteArray(32).also { SecureRandom().nextBytes(it) }).also { challenges += it }

    // The checks of WebAuthn §7.2, in its order (simplified)
    fun verify(a: Assertion): String {
        val c = Json.parseToJsonElement(a.clientDataJSON.decodeToString()).jsonObject.mapValues { it.value.jsonPrimitive.content }
        if (c["type"] != "webauthn.get") return "rejected: wrong type"
        if (!challenges.remove(c["challenge"])) return "rejected: unknown or used challenge"
        if (c["origin"] != origin) return "rejected: wrong origin ${c["origin"]}"
        if (!a.authenticatorData.copyOf(32).contentEquals(sha256(rpId.toByteArray()))) return "rejected: wrong RP ID"
        if (a.authenticatorData[32].toInt() and 0x01 == 0) return "rejected: user not present"
        val key = KeyFactory.getInstance("Ed25519").generatePublic(X509EncodedKeySpec(publicKeys[a.credentialId] ?: return "rejected: unknown credential"))
        val ok = Signature.getInstance("Ed25519").run { initVerify(key); update(a.authenticatorData + sha256(a.clientDataJSON)); verify(a.signature) }
        return if (ok) "accepted" else "rejected: bad signature"
    }
}

fun main() {
    val phone = Authenticator()
    val server = RelyingParty(rpId = "books.example", origin = "https://books.example")
    val (id, publicKey) = phone.register("books.example")
    server.store(id, publicKey)
    println("registered: the server stores a ${publicKey.size}-byte public key and no secret")

    val c1 = server.challenge()
    val good = phone.get(id, "books.example", clientData(c1, "https://books.example"))
    println("1 login at https://books.example        -> ${server.verify(good)}")

    // A phishing page fetches a real challenge and asks the user's browser to sign it. A real browser refuses outright,
    // because the passkey is scoped to books.example; this toy one obeys, so we can see the server's own check.
    val c2 = server.challenge()
    val phished = phone.get(id, "books.example", clientData(c2, "https://books-example.evil"))
    println("2 signed on https://books-example.evil  -> ${server.verify(phished)}")

    val c3 = server.challenge()
    val relabelled = phone.get(id, "books.example", clientData(c3, "https://books-example.evil"))
    val edited = Assertion(id, clientData(c3, "https://books.example"), relabelled.authenticatorData, relabelled.signature)
    println("3 same, origin edited to the real one   -> ${server.verify(edited)}")

    println("4 login 1 replayed                      -> ${server.verify(good)}")
}
