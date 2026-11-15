// Number of pairs i < j with a[i] > a[j]. Leaves a unchanged.
fun countInversions(a: IntArray): Long = sortAndCount(a.copyOf())

// Merge-sorts a in place and returns the number of inversions it had.
fun sortAndCount(a: IntArray): Long {
    if (a.size < 2) return 0                     // 0 or 1 items: sorted, no pairs
    val left = a.copyOfRange(0, a.size / 2)
    val right = a.copyOfRange(a.size / 2, a.size)
    var count = sortAndCount(left) + sortAndCount(right)   // pairs inside each half
    var i = 0; var j = 0; var k = 0
    while (i < left.size && j < right.size) {
        if (right[j] < left[i]) {
            count += left.size - i               // right[j] is smaller than every item left[i..] still waiting
            a[k++] = right[j++]
        } else {
            a[k++] = left[i++]                   // ties take from the left: equal is not an inversion
        }
    }
    while (i < left.size) a[k++] = left[i++]
    while (j < right.size) a[k++] = right[j++]
    return count
}
