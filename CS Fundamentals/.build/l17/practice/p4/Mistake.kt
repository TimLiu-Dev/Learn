class LastN<T>(capacity: Int) {
    init { require(capacity >= 1) { "capacity must be at least 1, was $capacity" } }

    private val items = arrayOfNulls<Any?>(capacity)
    private var next = 0
    var size = 0
        private set

    fun add(item: T) {
        items[next] = item
        next = (next + 1) % items.size
        if (size < items.size) size++
    }

    @Suppress("UNCHECKED_CAST")
    fun toList(): List<T> = List(size) { i -> items[i] as T }   // slot order, not age order
}
