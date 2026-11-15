inline fun <T> Iterable<T>.myFind(predicate: (T) -> Boolean): T? {
    var found: T? = null
    forEach {
        if (predicate(it)) {
            found = it
            return@forEach   // meant as "stop here", but it only skips to the next element
        }
    }
    return found
}

inline fun <T> Iterable<T>.myTakeWhile(predicate: (T) -> Boolean): List<T> {
    val result = ArrayList<T>()
    for (item in this) {
        if (!predicate(item)) break
        result.add(item)
    }
    return result
}
