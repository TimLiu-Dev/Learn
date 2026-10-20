// Counts valid e-mail addresses. Something in here is slow: let JFR find it.
const val EMAIL = "[a-z0-9.]+@[a-z0-9]+\\.[a-z]{2,}"

fun isValid(s: String): Boolean = Regex(EMAIL).matches(s)

fun main() {
    val inbox = List(2_000_000) { i ->
        if (i % 7 == 0) "not an email $i" else "user$i@example.com"
    }
    val start = System.nanoTime()
    var valid = 0
    repeat(3) {
        for (s in inbox) if (isValid(s)) valid++
    }
    println("$valid valid in ${(System.nanoTime() - start) / 1_000_000} ms")
}
