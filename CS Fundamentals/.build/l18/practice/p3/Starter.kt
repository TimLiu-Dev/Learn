// Number of pairs i < j with a[i] > a[j]. Leaves a unchanged.
fun countInversions(a: IntArray): Long = sortAndCount(a.copyOf())

// Merge-sorts a in place and returns the number of inversions it had.
fun sortAndCount(a: IntArray): Long =
    TODO("base case; split; count both halves recursively; merge while counting the pairs that cross")
