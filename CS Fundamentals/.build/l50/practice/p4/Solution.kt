// Delete elements from a failing input while it keeps failing; return a list where
// deleting any single element makes the failure go away.
fun <T> shrink(input: List<T>, fails: (List<T>) -> Boolean): List<T> {
    require(fails(input)) { "the input must fail to begin with" }
    var current = input
    var i = 0
    while (i < current.size) {
        val smaller = current.filterIndexed { j, _ -> j != i }   // current without element i
        if (fails(smaller)) {
            current = smaller
            i = 0                                        // something changed: earlier deletions may work now
        } else {
            i++                                          // element i is needed; try the next one
        }
    }
    return current
}
