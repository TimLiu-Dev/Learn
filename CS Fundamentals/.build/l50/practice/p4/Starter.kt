// Delete elements from a failing input while it keeps failing; return a list where
// deleting any single element makes the failure go away.
fun <T> shrink(input: List<T>, fails: (List<T>) -> Boolean): List<T> {
    TODO("check that input fails, then delete one element at a time while the result still fails")
}
