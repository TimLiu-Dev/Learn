fun main() {
    val weeks = listOf(listOf(30, 45), listOf(20), listOf(60, 15, 25))
    val nums = listOf(3, 1, 4, 1, 5, 9, 2, 6)

    println(weeks.flatMap { it })                       // flatten one level
    println(listOf("a", "b", "c").zip(listOf(1, 2, 3))) // pair up two lists
    println(nums.fold(0) { acc, n -> acc + n })         // start value 0
    println(nums.reduce { acc, n -> maxOf(acc, n) })    // starts from the first element
    println(listOf("7", "x", "42").mapNotNull { it.toIntOrNull() })
    println(nums.distinct())
    println("${nums.take(3)} ${nums.drop(5)}")
    println(nums.chunked(3))                            // fixed-size groups
    println(nums.windowed(3).map { it.sum() })          // sliding windows
    println(listOf("run", "yoga").associateWith { it.length })
    println(emptyList<Int>().reduceOrNull { a, b -> a + b })
    println(emptyList<Int>().reduce { a, b -> a + b })  // throws
}
