package katas

class MyArrayList<T>(initialCapacity: Int = 4) : Iterable<T> {
    private var data: Array<Any?>
    var size = 0
        private set
    val capacity: Int get() = data.size

    init {
        require(initialCapacity >= 1) { "initialCapacity must be at least 1, was $initialCapacity" }
        data = arrayOfNulls(initialCapacity)
    }

    fun isEmpty() = size == 0

    fun add(element: T) {                                   // amortised O(1)
        if (size == data.size) grow()
        data[size++] = element
    }

    fun add(index: Int, element: T) {                       // O(n): shifts the tail right
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index $index out of bounds for length $size")
        if (size == data.size) grow()
        data.copyInto(data, index + 1, index, size)         // overlapping copy is allowed
        data[index] = element
        size++
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(index: Int): T {                       // O(1)
        checkIndex(index)
        return data[index] as T
    }

    operator fun set(index: Int, element: T): T {           // O(1), returns the old value
        val old = get(index)
        data[index] = element
        return old
    }

    fun removeAt(index: Int): T {                           // O(n): shifts the tail left
        val old = get(index)
        data.copyInto(data, index, index + 1, size)
        data[--size] = null                                 // drop the stale reference (EJ Item 7)
        return old
    }

    private fun grow() {
        data = data.copyOf(data.size * 2)                   // new array, old elements copied: O(n)
    }

    private fun checkIndex(index: Int) {
        if (index !in 0 until size) throw IndexOutOfBoundsException("Index $index out of bounds for length $size")
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var next = 0
        override fun hasNext() = next < size
        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return get(next++)
        }
    }

    override fun toString() = joinToString(", ", "[", "]")
}
