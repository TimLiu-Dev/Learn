// Same program, with the pattern compiled once and reused.
private val EMAIL = Regex("[a-z0-9.]+@[a-z0-9]+\\.[a-z]{2,}")   // compiled once

fun isValid(s: String): Boolean = EMAIL.matches(s)

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
