fun main() {
    val lazy = sequenceOf(1, 2, 3, 4, 5, 6)
        .filter { print("f$it "); it % 2 == 0 }
        .map { print("m$it "); it * it }
        .first { it > 10 }
    println("-> $lazy")

    val eager = listOf(1, 2, 3, 4, 5, 6)
        .filter { print("f$it "); it % 2 == 0 }
        .map { print("m$it "); it * it }
        .first { it > 10 }
    println("-> $eager")
}
