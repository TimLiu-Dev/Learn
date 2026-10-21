import java.security.KeyPairGenerator
import java.security.Signature

fun main() {
    val pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
    val message = "pay Bob 10".toByteArray()

    // Only the private key can sign...
    val sig = Signature.getInstance("Ed25519").run {
        initSign(pair.private); update(message); sign()
    }
    println("signature: ${sig.size} bytes")

    // ...but anyone with the public key can verify
    fun checks(msg: ByteArray): Boolean = Signature.getInstance("Ed25519").run {
        initVerify(pair.public); update(msg); verify(sig)
    }
    println("original verifies: ${checks(message)}")
    println("altered verifies:  ${checks("pay Bob 1000".toByteArray())}")
}
