fun main() {
    var count = 0
    for (i in Int.MAX_VALUE - 2..Int.MAX_VALUE) count++
    println("Kotlin loop ran $count times")
}
