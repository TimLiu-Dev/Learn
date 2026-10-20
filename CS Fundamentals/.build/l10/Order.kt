fun step(n: Int): Int {
    try {
        println("try $n")
        if (n == 2) throw IllegalStateException("boom $n")
        return n * 10
    } catch (e: IllegalStateException) {
        println("catch ${e.message}")
        return -1
    } finally {
        println("finally $n")
    }
}

fun main() {
    println(step(1))
    println(step(2))
}
