// p → q is false on exactly one row: p true and q false.
fun implies(p: Boolean, q: Boolean): Boolean = !p || q

private val bools = listOf(false, true)

// Two nested loops visit all four (p, q) pairs: that's the whole proof.
fun equivalent(f: (Boolean, Boolean) -> Boolean, g: (Boolean, Boolean) -> Boolean): Boolean =
    bools.all { p -> bools.all { q -> f(p, q) == g(p, q) } }

fun alwaysTrue(f: (Boolean, Boolean) -> Boolean): Boolean = equivalent(f) { _, _ -> true }
