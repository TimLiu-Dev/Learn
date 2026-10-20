fun add(a: Int, b: Int): Int {
    return a + b
}

fun sumTo(n: Int): Int {
    var total = 0
    for (i in 1..n) {
        total += i
    }
    return total
}

fun main() {
    println("${add(2, 3)} ${sumTo(4)}")
}
