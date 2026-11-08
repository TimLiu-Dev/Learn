fun main() {
    val r = listOf(1, 2, 3)
        .onEach { print("a$it ") }
        .asSequence()
        .map { print("b$it "); it * 2 }
        .first { it >= 4 }
    println("= $r")
}
