fun main() {
    val a = (1..100).sumOf { it * it }                        // IntRange: a plain int loop
    val b = (1..100).asSequence().map { it * it }.sum()       // Sequence<Int>: boxed Integers
    println("$a $b")

    val scores = intArrayOf(4, 8, 15, 16, 23, 42)              // IntArray = Java int[]
    println("min=${scores.min()} max=${scores.max()} avg=${scores.average()}")
}
