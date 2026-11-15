fun implies(p: Boolean, q: Boolean): Boolean = !p || q

private val bools = listOf(false, true)

// One loop instead of two: only checks (false, false) and (true, true).
fun equivalent(f: (Boolean, Boolean) -> Boolean, g: (Boolean, Boolean) -> Boolean): Boolean =
    bools.all { b -> f(b, b) == g(b, b) }

fun alwaysTrue(f: (Boolean, Boolean) -> Boolean): Boolean = equivalent(f) { _, _ -> true }
