class Res(private val name: String) : AutoCloseable {
    override fun close() {
        print("close $name, ")
        throw IllegalStateException("close $name failed")
    }
}

fun main() {
    try {
        Res("A").use {
            Res("B").use {
                throw IllegalStateException("body failed")
            }
        }
    } catch (e: IllegalStateException) {
        println("caught ${e.message} (+${e.suppressed.size} suppressed)")
    }
}
