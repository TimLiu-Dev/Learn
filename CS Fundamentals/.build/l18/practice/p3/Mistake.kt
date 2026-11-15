fun countInversions(a: IntArray): Long = sortAndCount(a.copyOf())

fun sortAndCount(a: IntArray): Long {
    if (a.size < 2) return 0
    val left = a.copyOfRange(0, a.size / 2)
    val right = a.copyOfRange(a.size / 2, a.size)
    var count = sortAndCount(left) + sortAndCount(right)
    var i = 0; var j = 0; var k = 0
    while (i < left.size && j < right.size) {
        if (right[j] <= left[i]) {               // ties taken from the right are counted as inversions
            count += left.size - i
            a[k++] = right[j++]
        } else {
            a[k++] = left[i++]
        }
    }
    while (i < left.size) a[k++] = left[i++]
    while (j < right.size) a[k++] = right[j++]
    return count
}
