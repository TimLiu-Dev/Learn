// n + sumTo(n - 1): the addition happens AFTER the call returns, so it isn't a tail call.
tailrec fun sumTo(n: Long): Long = if (n == 0L) 0 else n + sumTo(n - 1)

fun main() {
    println(sumTo(10))
}
