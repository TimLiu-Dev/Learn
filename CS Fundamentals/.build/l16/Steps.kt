// Count the steps instead of timing them: the count depends only on n.
fun constant(n: Int): Long = 1                     // one step, whatever n is

fun linear(n: Int): Long {
    var steps = 0L
    for (i in 0 until n) steps++                   // n steps
    return steps
}

fun nested(n: Int): Long {
    var steps = 0L
    for (i in 0 until n)
        for (j in 0 until n) steps++               // n * n steps
    return steps
}

fun halving(n: Int): Long {
    var steps = 0L
    var k = n
    while (k > 1) { k /= 2; steps++ }              // how many times can n be halved?
    return steps
}

fun main() {
    println("%8s %6s %8s %10s %14s".format("n", "O(1)", "O(log n)", "O(n)", "O(n^2)"))
    for (n in listOf(10, 100, 1_000, 10_000, 100_000)) {
        println("%8d %6d %8d %10d %14d".format(n, constant(n), halving(n), linear(n), nested(n)))
    }
}
