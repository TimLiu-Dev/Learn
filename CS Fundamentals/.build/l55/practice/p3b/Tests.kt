import java.nio.ByteBuffer
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.Signature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PasskeyTest {
    private val pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
    private val challenge = "c3-Kq9vXwT0"

    private fun clientData(type: String = "webauthn.get", ch: String = challenge, origin: String = ORIGIN) =
        """{"type":"$type","challenge":"$ch","origin":"$origin"}""".toByteArray()

    private fun authData(rpId: String = RP_ID, flags: Int = 0x05) =
        ByteBuffer.allocate(37).put(sha256(rpId.toByteArray())).put(flags.toByte()).putInt(1).array()

    private fun sign(ad: ByteArray, cd: ByteArray, key: PrivateKey = pair.private) =
        Signature.getInstance("Ed25519").run { initSign(key); update(ad + sha256(cd)); sign() }

    private fun assertion(cd: ByteArray = clientData(), ad: ByteArray = authData()) = Assertion(cd, ad, sign(ad, cd))

    @Test fun `a genuine login is accepted`() {
        assertNull(problem(assertion(), challenge, pair.public))
    }

    @Test fun `a signature made on another origin is rejected, however close the name`() {
        for (o in listOf("https://books-example.evil", "https://evilbooks.example", "http://books.example", "https://books.example.evil"))
            assertEquals("origin $o", "wrong origin", problem(assertion(clientData(origin = o)), challenge, pair.public))
    }

    @Test fun `the type and challenge must be this login's`() {
        assertEquals("type webauthn.create", "wrong type", problem(assertion(clientData(type = "webauthn.create")), challenge, pair.public))
        assertEquals("an old challenge", "wrong challenge", problem(assertion(clientData(ch = "old-challenge")), challenge, pair.public))
    }

    @Test fun `the authenticator data must name this RP and a present user`() {
        assertEquals("RP ID evil.example", "wrong RP ID", problem(assertion(ad = authData(rpId = "evil.example")), challenge, pair.public))
        assertEquals("flags 0x00", "user not present", problem(assertion(ad = authData(flags = 0x00)), challenge, pair.public))
    }

    @Test fun `edited client data or another key breaks the signature`() {
        val evil = clientData(origin = "https://books-example.evil")
        val signedOnEvil = sign(authData(), evil)
        assertEquals("origin edited after signing", "bad signature",
            problem(Assertion(clientData(), authData(), signedOnEvil), challenge, pair.public))
        val other = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
        assertEquals("signed by another key", "bad signature",
            problem(Assertion(clientData(), authData(), sign(authData(), clientData(), other.private)), challenge, pair.public))
    }
}
