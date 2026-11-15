// -1 (all ones) when x < 0, otherwise 0. shr copies the sign bit into every position.
fun signMask(x: Int): Int = x shr 31

// The smaller of a and b, with no branch: no if, when, <, > or minOf.
fun minBranchless(a: Int, b: Int): Int {
    // a - b can overflow an Int (L2), which flips its sign. As a Long it can't.
    val mask = ((a.toLong() - b) shr 63).toInt()   // all ones when a < b, else 0
    return b xor ((a xor b) and mask)               // mask all ones: b xor a xor b = a. Mask 0: b.
}
