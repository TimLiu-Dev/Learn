data class Point(val x: Int, val y: Int)

// The String.hashCode formula from the JDK docs, by hand
fun hashByHand(s: String): Int {
    var h = 0
    for (c in s) h = 31 * h + c.code       // Int arithmetic: overflow just wraps
    return h
}

fun main() {
    println("by hand: ${hashByHand("cat")}  built-in: ${"cat".hashCode()}")
    println("c*31^2 + a*31 + t = ${99 * 31 * 31 + 97 * 31 + 116}")
    println("banana: ${"banana".hashCode()}")              // wrapped past Int.MAX_VALUE
    println("Aa=${"Aa".hashCode()} BB=${"BB".hashCode()}")  // different strings, same hash
    println(Point(1, 2).hashCode() == Point(1, 2).hashCode())
    println("Point(1, 2): ${Point(1, 2).hashCode()}")
}
