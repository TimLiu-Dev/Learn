class Counter(var count: Int)

fun main() {
    var x = 1
    var y = x          // copies the number
    y = 99
    println("x=$x y=$y")

    val a = Counter(1)
    val b = a          // copies the reference: one object, two arrows
    b.count = 99
    println("a.count=${a.count} b.count=${b.count}")

    var c = a
    c = Counter(5)     // re-points c; a and the old object are untouched
    println("a.count=${a.count} c.count=${c.count}")
}
