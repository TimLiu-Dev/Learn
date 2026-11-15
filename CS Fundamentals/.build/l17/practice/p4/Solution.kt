class LastN<T>(capacity: Int) {
    init { require(capacity >= 1) { "capacity must be at least 1, was $capacity" } }

    private val items = arrayOfNulls<Any?>(capacity)
    private var next = 0                            // the slot the next add writes to
    var size = 0
        private set

    fun add(item: T) {                              // O(1), never grows
        items[next] = item                          // when full, this slot holds the oldest item
        next = (next + 1) % items.size
        if (size < items.size) size++
    }

    fun toList(): List<T> {
        val oldest = (next - size + items.size) % items.size   // 0 until full, then next
        @Suppress("UNCHECKED_CAST")
        return List(size) { i -> items[(oldest + i) % items.size] as T }
    }
}
