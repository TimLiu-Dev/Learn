// p → q is false on exactly one row: p true and q false.
fun implies(p: Boolean, q: Boolean): Boolean = TODO("write it with ! and ||")

// True when f and g give the same answer for every one of the four (p, q) pairs.
fun equivalent(f: (Boolean, Boolean) -> Boolean, g: (Boolean, Boolean) -> Boolean): Boolean =
    TODO("compare f and g on all four rows of the truth table")

// True when f is true on every row.
fun alwaysTrue(f: (Boolean, Boolean) -> Boolean): Boolean = TODO("check every row, or reuse equivalent")
