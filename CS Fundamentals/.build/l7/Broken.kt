class BadPoint(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean =
        other is BadPoint && x == other.x && y == other.y
    // hashCode() NOT overridden: each object keeps its identity-based hash
}

fun main() {
    val set = hashSetOf(BadPoint(1, 2))
    println(BadPoint(1, 2) == BadPoint(1, 2))   // equals says yes...
    println(set.contains(BadPoint(1, 2)))       // ...the HashSet says no
    set.add(BadPoint(1, 2))
    println(set.size)                           // a "duplicate" got in

    val names = hashMapOf(BadPoint(0, 0) to "origin")
    println(names[BadPoint(0, 0)])              // lookup by an equal key fails
}
