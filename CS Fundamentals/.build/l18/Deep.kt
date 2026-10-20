var depth = 0

fun dive() {                     // no base case at all
    depth++
    dive()
}

fun sumTo(n: Long): Long = if (n == 0L) 0 else n + sumTo(n - 1)

fun sumToLoop(n: Long): Long {
    var total = 0L
    for (i in 1..n) total += i
    return total
}

fun main() {
    try {
        dive()
    } catch (e: StackOverflowError) {           // demo only: never catch this in real code
        println("dive(): StackOverflowError after $depth frames")
    }
    println("sumTo(10_000) = ${sumTo(10_000)}")
    try {
        println(sumTo(1_000_000))
    } catch (e: StackOverflowError) {
        println("sumTo(1_000_000): StackOverflowError")
    }
    println("sumToLoop(1_000_000) = ${sumToLoop(1_000_000)}")
}
