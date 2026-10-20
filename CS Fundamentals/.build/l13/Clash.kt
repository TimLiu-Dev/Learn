fun label(xs: List<Int>): String = "ints: $xs"

@JvmName("labelStrings")                                 // a different JVM name (L9)
fun label(xs: List<String>): String = "strings: $xs"

fun main() {
    println(label(listOf(1, 2)))                         // Kotlin callers still write label(...)
    println(label(listOf("a", "b")))
}
