var calls = 0L

fun fib(n: Int): Long {
    calls++
    return if (n < 2) n.toLong() else fib(n - 1) + fib(n - 2)
}

fun main() {
    for (n in listOf(10, 20, 30, 40, 45)) {
        calls = 0
        val start = System.nanoTime()
        val f = fib(n)
        val ms = (System.nanoTime() - start) / 1_000_000
        println("fib(%d) = %,d   calls = %,d   %d ms".format(n, f, calls, ms))
    }
}
