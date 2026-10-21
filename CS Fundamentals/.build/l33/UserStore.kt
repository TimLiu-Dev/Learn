import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class UserStore(private val iterations: Int = 600_000) {
    private val users = mutableMapOf<String, String>()  // name -> stored hash string
    private val b64 = Base64.getEncoder()
    private val unb64 = Base64.getDecoder()

    private fun pbkdf2(pw: String, salt: ByteArray, iter: Int): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(pw.toCharArray(), salt, iter, 256)).encoded

    private fun hash(pw: String, iter: Int): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return "pbkdf2-sha256\$$iter\$${b64.encodeToString(salt)}\$${b64.encodeToString(pbkdf2(pw, salt, iter))}"
    }

    fun register(name: String, pw: String, iter: Int = iterations) {
        require(name !in users) { "name taken: $name" }
        require(pw.length >= 8) { "password too short" }
        users[name] = hash(pw, iter)
    }

    fun login(name: String, pw: String): Boolean {
        val stored = users[name] ?: return false
        val (_, iter, salt, expected) = stored.split("$")
        val ok = MessageDigest.isEqual(pbkdf2(pw, unb64.decode(salt), iter.toInt()), unb64.decode(expected))
        if (ok && iter.toInt() < iterations) {
            users[name] = hash(pw, iterations)  // upgrade on the next successful login
            println("  upgraded $name from $iter to $iterations iterations")
        }
        return ok
    }

    fun costOf(name: String) = users.getValue(name).split("$")[1]
}

fun main() {
    val store = UserStore()
    store.register("ana", "correct horse")
    store.register("old", "legacy pass", iter = 100_000)  // created years ago
    println("ana right: ${store.login("ana", "correct horse")}")
    println("ana wrong: ${store.login("ana", "correct hors")}")
    println("ghost:     ${store.login("ghost", "anything at all")}")
    println("old cost before: ${store.costOf("old")}")
    println("old login: ${store.login("old", "legacy pass")}")
    println("old cost after:  ${store.costOf("old")}")
    try { store.register("ana", "another pass") } catch (e: IllegalArgumentException) { println("register: ${e.message}") }
}
