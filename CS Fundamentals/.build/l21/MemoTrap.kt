val memo = HashMap<Int, Long>()

// Java's computeIfAbsent, called from Kotlin: same trap.
fun fibTrap(n: Int): Long {
    if (n < 2) return n.toLong()
    return memo.computeIfAbsent(n) { k -> fibTrap(k - 1) + fibTrap(k - 2) }
}

// Kotlin's getOrPut is a plain get, then (if missing) compute, then put. Safe.
fun fibSafe(n: Int): Long {
    if (n < 2) return n.toLong()
    return memo.getOrPut(n) { fibSafe(n - 1) + fibSafe(n - 2) }
}

fun main() {
    println(fibSafe(30))
    memo.clear()
    println(fibTrap(30))
}
