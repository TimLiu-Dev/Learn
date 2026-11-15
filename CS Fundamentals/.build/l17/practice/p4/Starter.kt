class LastN<T>(capacity: Int) {
    private val items = arrayOfNulls<Any?>(capacity)
    private var next = 0                            // the slot the next add writes to
    var size = 0
        private set

    fun add(item: T) {
        TODO("write at next, move next on, and count up to the capacity")
    }

    fun toList(): List<T> = TODO("oldest first: where is the oldest item once the buffer has wrapped?")
}
