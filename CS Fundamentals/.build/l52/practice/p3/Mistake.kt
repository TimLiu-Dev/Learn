fun signMask(x: Int): Int = x shr 31

// The mistake: a - b overflows when a and b are far apart, and the sign lies.
fun minBranchless(a: Int, b: Int): Int {
    val mask = signMask(a - b)
    return b xor ((a xor b) and mask)
}
