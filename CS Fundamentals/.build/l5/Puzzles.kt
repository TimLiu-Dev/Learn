fun main() {
    val x: Int? = 1000
    val y: Int? = 1000
    println(x == y)                                  // P1

    val first = mutableListOf(1, 2)
    val second = first
    second += 3
    println(first)                                   // P2

    println(intArrayOf(1, 2).contentEquals(intArrayOf(1, 2)))   // P3

    val frozen: List<Int> = first
    first.clear()
    println(frozen.size)                             // P4
}
