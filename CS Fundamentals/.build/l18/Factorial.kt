fun factorial(n: Int): Long {
    if (n <= 1) return 1                 // base case: stop here
    return n * factorial(n - 1)          // recursive case: a smaller problem
}

fun main() {
    val f = factorial(4)
    println(f)
}
