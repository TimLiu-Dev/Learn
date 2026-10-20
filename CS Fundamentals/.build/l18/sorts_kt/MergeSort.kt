// Split in half, sort each half recursively, merge the two sorted halves.
fun mergeSort(a: IntArray) {
    if (a.size < 2) return                       // base case: 0 or 1 items are sorted
    val left = a.copyOfRange(0, a.size / 2)      // O(n) extra space
    val right = a.copyOfRange(a.size / 2, a.size)
    mergeSort(left)
    mergeSort(right)
    var i = 0; var j = 0; var k = 0
    while (i < left.size && j < right.size) {
        // take from right only if strictly smaller: ties come from left (stable)
        a[k++] = if (less(right[j], left[i])) right[j++] else left[i++]
    }
    while (i < left.size) a[k++] = left[i++]     // copy whatever is left over
    while (j < right.size) a[k++] = right[j++]
}
