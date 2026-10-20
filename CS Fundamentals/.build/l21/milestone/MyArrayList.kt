// Minimal stand-in for your L16 MyArrayList. Swap in your own.
class MyArrayList<T> {
    private var data = arrayOfNulls<Any?>(8)
    var size = 0
        private set

    fun add(x: T) {
        if (size == data.size) data = data.copyOf(size * 2)   // double: amortised O(1)
        data[size++] = x
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(i: Int): T {
        if (i !in 0 until size) throw IndexOutOfBoundsException("index $i, size $size")
        return data[i] as T
    }
}
