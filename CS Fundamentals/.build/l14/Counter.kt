fun makeCounter(): () -> Int {
    var count = 0
    return { count += 1; count }   // the lambda captures `count`
}

fun main() {
    val a = makeCounter()
    val b = makeCounter()
    a()
    a()
    println(a())
    println(b())
}
