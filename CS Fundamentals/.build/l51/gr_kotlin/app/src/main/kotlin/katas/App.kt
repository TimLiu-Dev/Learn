package katas

import kotlin.time.measureTime

private fun time(label: String, block: () -> Boolean) {
    var result = false
    val t = measureTime { result = block() }
    println("  %-34s %-5s %9.3f ms".format(label, result, t.inWholeMicroseconds / 1000.0))
}

fun main() {
    repeat(200) { TinyRegex("(a|b)*abb").matches("abababb") }      // warm-up
    for (n in listOf(24, 26)) {
        val p = "a?".repeat(n) + "a".repeat(n)
        val s = "a".repeat(n)
        println("a?{$n}a{$n} against a{$n}:")
        time("java.util.regex (backtracking)") { Regex(p).matches(s) }
        time("TinyRegex (${TinyRegex(p).stateCount} NFA states)") { TinyRegex(p).matches(s) }
    }
    val p = "(.*a)".repeat(12)
    val s = "a".repeat(28) + "x"
    println("(.*a) twelve times against a{28}x:")
    time("java.util.regex (backtracking)") { Regex(p).matches(s) }
    time("TinyRegex") { TinyRegex(p).matches(s) }
    val long = "a".repeat(1_000_000) + "b"
    println("(a+)+b against a million a's then b:")
    time("TinyRegex") { TinyRegex("(a+)+b").matches(long) }
}
