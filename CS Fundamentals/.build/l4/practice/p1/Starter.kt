// Correct for small n, but each call waits in its own frame for the next one to return.
fun sumTo(n: Int): Long =
    if (n == 0) 0 else n + sumTo(n - 1)
