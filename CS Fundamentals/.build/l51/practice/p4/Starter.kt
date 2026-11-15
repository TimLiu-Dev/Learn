// Subset sum. A proposed answer (a certificate) is a list of item indices.

// The verifier: one pass over the certificate.
fun verify(sizes: IntArray, target: Int, pick: List<Int>): Boolean = TODO("indices valid, no repeats, right sum")

// The searcher: up to 2^n subsets, each one checked by verify.
fun search(sizes: IntArray, target: Int): List<Int>? = TODO("bit i of the mask means item i is in")
