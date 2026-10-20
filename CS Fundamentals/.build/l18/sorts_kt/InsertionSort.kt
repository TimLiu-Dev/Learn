// Grow a sorted prefix a[0..i-1]; slide a[i] left into place.
fun insertionSort(a: IntArray) {
    for (i in 1 until a.size) {
        val key = a[i]
        var j = i - 1
        while (j >= 0 && less(key, a[j])) {      // shift bigger items one step right
            a[j + 1] = a[j]
            j--
        }
        a[j + 1] = key                           // drop key into the gap
    }
}
