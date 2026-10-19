fun square(x: Int): Int {
    val result = x * x
    return result
}

fun sumOfSquares(a: Int, b: Int): Int {
    val sa = square(a)
    val sb = square(b)
    return sa + sb
}

fun main() {
    val total = sumOfSquares(3, 4)
    println(total)
}
