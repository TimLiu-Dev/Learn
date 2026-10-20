fun label(xs: List<Int>): String = "ints: $xs"
fun label(xs: List<String>): String = "strings: $xs"    // same JVM signature after erasure

fun main() = println(label(listOf(1, 2)))
