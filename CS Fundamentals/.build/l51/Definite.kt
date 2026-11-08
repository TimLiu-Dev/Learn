fun pick(n: Int): Int {
    val x: Int
    if (n == n) x = 1          // always true for an Int, but the compiler doesn't reason about values
    return x
}

fun main() = println(pick(7))
