// Subset sum. A proposed answer (a certificate) is a list of item indices.

// The verifier: one pass over the certificate.
fun verify(sizes: IntArray, target: Int, pick: List<Int>): Boolean =
    pick.all { it in sizes.indices } &&              // check the range before indexing
        pick.toSet().size == pick.size &&            // no item used twice
        pick.sumOf { sizes[it] } == target

// The searcher: up to 2^n subsets, each one checked by verify.
fun search(sizes: IntArray, target: Int): List<Int>? {
    for (mask in 0 until (1 shl sizes.size)) {
        val pick = sizes.indices.filter { mask and (1 shl it) != 0 }
        if (verify(sizes, target, pick)) return pick
    }
    return null
}
