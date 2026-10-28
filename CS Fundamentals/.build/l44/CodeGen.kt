// Three ways to turn a long URL into a short code.
import java.security.MessageDigest
import java.security.SecureRandom

const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

fun encode(n: Long): String {
    require(n >= 0) { "negative: $n" }
    if (n == 0L) return "0"
    val sb = StringBuilder()
    var x = n
    while (x > 0) {
        sb.append(ALPHABET[(x % 62).toInt()])
        x /= 62
    }
    return sb.reverse().toString()
}

fun decode(code: String): Long =
    code.fold(0L) { acc, c ->
        val digit = ALPHABET.indexOf(c)
        require(digit >= 0) { "not base62: '$c'" }
        acc * 62 + digit
    }

// 1. Counter: the database hands out 1, 2, 3, ...; the code is just the id in base 62.
fun counterCode(id: Long) = encode(id)

// 2. Hash and truncate: same URL, same code, but different URLs can collide.
fun hashCode7(url: String): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(url.toByteArray())
    var n = 0L
    for (i in 0 until 8) n = (n shl 8) or (digest[i].toLong() and 0xff)
    return encode(Math.floorMod(n, 3_521_614_606_208L)).padStart(7, '0')   // 62^7
}

// 3. Random: unguessable; the caller must retry if the code is already taken.
private val rng = SecureRandom()
fun randomCode(length: Int = 7) = String(CharArray(length) { ALPHABET[rng.nextInt(62)] })

fun main() {
    for (n in listOf(0L, 61L, 62L, 3843L, 3844L, 125_000_000_000L, Long.MAX_VALUE)) {
        val code = encode(n)
        check(decode(code) == n) { "round trip failed for $n" }
        println("%-20d -> %s".format(n, code))
    }
    println("counter ids 1000..1004 -> " + (1000L..1004L).map(::counterCode))
    println("hash of /a twice       -> " + hashCode7("https://example.com/a") + " " + hashCode7("https://example.com/a"))
    println("hash of /b             -> " + hashCode7("https://example.com/b"))
    println("random x3              -> " + List(3) { randomCode() })
}
