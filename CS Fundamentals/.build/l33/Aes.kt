import java.security.SecureRandom
import javax.crypto.AEADBadTagException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

fun encrypt(key: SecretKey, iv: ByteArray, plain: ByteArray): ByteArray =
    Cipher.getInstance("AES/GCM/NoPadding").run {
        init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        doFinal(plain)
    }

fun decrypt(key: SecretKey, iv: ByteArray, sealed: ByteArray): ByteArray =
    Cipher.getInstance("AES/GCM/NoPadding").run {
        init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        doFinal(sealed)
    }

fun main() {
    val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }  // never reuse with the same key
    val sealed = encrypt(key, iv, "meet at noon".toByteArray())
    println("ciphertext+tag: ${sealed.size} bytes")
    println("decrypted: ${String(decrypt(key, iv, sealed))}")

    sealed[0] = (sealed[0].toInt() xor 1).toByte()  // flip one bit
    try {
        decrypt(key, iv, sealed)
    } catch (e: AEADBadTagException) {
        println("tampered: ${e.javaClass.simpleName}: ${e.message}")
    }
}
