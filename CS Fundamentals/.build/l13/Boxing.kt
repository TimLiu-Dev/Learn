fun main() {
    val big: List<Int> = listOf(1000, 1000)      // you write Int; the list stores boxes
    println((big[0] as Any).javaClass.name)      // what's really inside
    println(big[0] == big[1])                    // == calls equals(): safe
}
