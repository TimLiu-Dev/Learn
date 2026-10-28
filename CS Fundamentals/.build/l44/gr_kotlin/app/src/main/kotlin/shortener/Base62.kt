package shortener

import java.security.SecureRandom

object Base62 {
    private const val ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val rng = SecureRandom()

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

    fun decode(code: String): Long = code.fold(0L) { acc, c ->
        val digit = ALPHABET.indexOf(c)
        require(digit >= 0) { "not base62: '$c'" }
        acc * 62 + digit
    }

    fun random(length: Int): String = String(CharArray(length) { ALPHABET[rng.nextInt(62)] })
}
