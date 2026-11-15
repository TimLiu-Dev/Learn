// The first element that matches, or null. Stop as soon as you find it.
inline fun <T> Iterable<T>.myFind(predicate: (T) -> Boolean): T? =
    TODO("loop, and return the item as soon as predicate says yes")

// The leading elements while predicate holds
inline fun <T> Iterable<T>.myTakeWhile(predicate: (T) -> Boolean): List<T> =
    TODO("collect items into a list until the first miss")
