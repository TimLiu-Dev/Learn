fun factorial(n: Int): Long =
    if (n <= 1) 1 else n * factorial(n - 1)

// sum of a list = first element + sum of the rest
fun sum(xs: List<Int>): Int =
    if (xs.isEmpty()) 0 else xs[0] + sum(xs.subList(1, xs.size))

// reverse of a string = reverse of the rest + first char
fun reverse(s: String): String =
    if (s.length <= 1) s else reverse(s.substring(1)) + s[0]

fun main() {
    println(factorial(5))
    println(factorial(20))
    println(sum(listOf(3, 1, 4, 1, 5)))
    println(sum(emptyList()))
    println(reverse("stressed"))
}
