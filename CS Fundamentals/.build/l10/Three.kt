class Res(private val name: String) : AutoCloseable {
    init { println("open $name") }
    override fun close() = println("close $name")
}

fun main() {
    Res("X").use { x ->
        Res("Y").use { y ->
            Res("Z").use { z ->
                println("using X, Y, Z")
            }
        }
    }
}
