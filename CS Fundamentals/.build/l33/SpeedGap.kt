import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun main() {
    val md = MessageDigest.getInstance("SHA-256")
    val pw = "sunshine1".toByteArray()
    var sink = 0
    repeat(200_000) { sink += md.digest(pw)[0] }  // warm-up
    val n = 1_000_000
    var t = System.nanoTime()
    repeat(n) { sink += md.digest(pw)[0] }
    val shaNs = (System.nanoTime() - t) / n

    val f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val salt = ByteArray(16)
    f.generateSecret(PBEKeySpec("w".toCharArray(), salt, 600_000, 256))  // warm-up
    t = System.nanoTime()
    repeat(5) { sink += f.generateSecret(PBEKeySpec("sunshine1".toCharArray(), salt, 600_000, 256)).encoded[0] }
    val pbNs = (System.nanoTime() - t) / 5

    println("SHA-256, one guess:   $shaNs ns  (~${1_000_000_000 / shaNs} guesses/s on one core)")
    println("PBKDF2 600k, one guess: ${pbNs / 1_000_000} ms  (~${1_000_000_000 / pbNs} guesses/s)")
    println("slowdown: ~${pbNs / shaNs}x   (sink $sink)")
}
