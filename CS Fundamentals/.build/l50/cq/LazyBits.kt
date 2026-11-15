var built = 0

fun bitStrings(n: Int): Sequence<String> = sequence {
    suspend fun SequenceScope<String>.go(prefix: String) {
        if (prefix.length == n) { built++; yield(prefix); return }
        go(prefix + "0")
        go(prefix + "1")
    }
    go("")
}

fun main() {
    val firstTwo = bitStrings(3).take(2).toList()
    println("$firstTwo built=$built")
}
