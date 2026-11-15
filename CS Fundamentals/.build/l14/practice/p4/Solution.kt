inline fun <T> Iterable<T>.myFind(predicate: (T) -> Boolean): T? {
    for (item in this) if (predicate(item)) return item   // stops the loop and the function
    return null
}

inline fun <T> Iterable<T>.myTakeWhile(predicate: (T) -> Boolean): List<T> {
    val result = ArrayList<T>()
    for (item in this) {
        if (!predicate(item)) break
        result.add(item)
    }
    return result
}
