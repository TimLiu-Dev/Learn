fun mul3(a: Int, b: Int, c: Int): Int {
    return a * b * c
}

fun countDown(start: Int): Int {
    var n = start
    var steps = 0
    while (n > 0) {
        n -= 2
        steps++
    }
    return steps
}

fun main() {
    println("${mul3(2, 3, 4)} ${countDown(7)}")
}
