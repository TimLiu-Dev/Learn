class MyQueue<T>(initialCapacity: Int = 4) {
    private var items = arrayOfNulls<Any?>(initialCapacity)
    private var head = 0                            // index of the front element
    private var tail = 0                            // index of the next free slot
    var size = 0
        private set

    fun offer(item: T) {                            // amortised O(1)
        if (size == items.size) grow()
        items[tail] = item
        tail = (tail + 1) % items.size              // wrap past the end back to 0
        size++
    }

    fun poll(): T? {                                // O(1); null when empty, like Queue.poll
        if (size == 0) return null
        @Suppress("UNCHECKED_CAST")
        val item = items[head] as T
        items[head] = null                          // let the GC have it (L4)
        head = (head + 1) % items.size
        size--
        return item
    }

    @Suppress("UNCHECKED_CAST")
    fun peek(): T? = if (size == 0) null else items[head] as T

    private fun grow() {                            // O(n), but only when full
        val bigger = arrayOfNulls<Any?>(items.size * 2)
        for (i in 0 until size) bigger[i] = items[(head + i) % items.size]  // unwrap in order
        items = bigger
        head = 0
        tail = size
    }

    fun debug() = "items=${items.contentToString()} head=$head tail=$tail size=$size"
}
