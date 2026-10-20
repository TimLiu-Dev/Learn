// Give the Big O of each function in terms of n (the input size). m = size of the second input.
fun last(xs: List<Int>): Int = xs[xs.size - 1]                  // (a)

fun countDigits(n: Int): Int {                                    // (b) n is the number itself
    var k = n
    var digits = 1
    while (k >= 10) { k /= 10; digits++ }
    return digits
}

fun hasPairSum(xs: IntArray, target: Int): Boolean {              // (c)
    for (i in xs.indices)
        for (j in i + 1 until xs.size)
            if (xs[i] + xs[j] == target) return true
    return false
}

fun sumAndMax(xs: IntArray): Pair<Int, Int> {                     // (d)
    var sum = 0
    for (x in xs) sum += x
    var max = Int.MIN_VALUE
    for (x in xs) if (x > max) max = x
    return sum to max
}

fun commonCount(a: List<String>, b: List<String>): Int =          // (e)
    a.count { it in b }

fun triangle(n: Int): Int {                                       // (f)
    var stars = 0
    for (i in 0 until n)
        for (j in 0..i) stars++
    return stars
}

fun firstNegative(xs: IntArray): Int {                            // (g) best case? worst case?
    for (i in xs.indices) if (xs[i] < 0) return i
    return -1
}

fun main() {
    println(last(listOf(3, 1, 4)))
    println(countDigits(90210))
    println(hasPairSum(intArrayOf(3, 8, 5), 13))
    println(sumAndMax(intArrayOf(3, 8, 5)))
    println(commonCount(listOf("a", "b", "c"), listOf("b", "c", "d")))
    println(triangle(4))
    println(firstNegative(intArrayOf(-2, 5, 7)))
}
