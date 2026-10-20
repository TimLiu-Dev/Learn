// The recursive call is the LAST thing the function does: a tail call.
tailrec fun sumTo(n: Long, acc: Long = 0): Long =
    if (n == 0L) acc else sumTo(n - 1, acc + n)

fun main() {
    println(sumTo(1_000_000))
    println(sumTo(100_000_000))
}
