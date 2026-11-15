// A stack on a resizing array: doubles when full, halves when a quarter full.
class ResizingStack<T> {
    private var data = arrayOfNulls<Any?>(2)
    var size = 0
        private set
    val capacity: Int get() = data.size
    var copies = 0L                                   // elements copied by resizes so far
        private set

    private fun resize(newCapacity: Int) {
        copies += size
        data = data.copyOf(newCapacity)               // a new array, holding the first `size` elements
    }

    fun push(x: T) {
        if (size == data.size) resize(data.size * 2)
        data[size++] = x
    }

    @Suppress("UNCHECKED_CAST")                       // safe: only push puts things in data, and they're all T
    fun pop(): T {
        if (size == 0) throw NoSuchElementException("pop on an empty stack")
        val top = data[--size] as T
        data[size] = null                             // don't keep a removed element alive (Effective Java Item 7)
        if (size > 0 && size == data.size / 2) resize(data.size / 2)   // half full: shrink right away
        return top
    }
}
