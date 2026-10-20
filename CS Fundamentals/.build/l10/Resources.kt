class Res(private val name: String, private val failOnClose: Boolean = false) : AutoCloseable {
    init { println("open $name") }

    override fun close() {
        println("close $name")
        if (failOnClose) throw IllegalStateException("close $name failed")
    }
}

fun main() {
    Res("A").use { a ->
        Res("B").use { b ->
            println("using A and B")
        }
    }

    try {
        Res("C", failOnClose = true).use { c ->
            throw IllegalStateException("body failed")
        }
    } catch (e: IllegalStateException) {
        println("caught: ${e.message}")
        for (s in e.suppressed) println("  suppressed: ${s.message}")
    }
}
