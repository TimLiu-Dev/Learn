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

    fun pop(): T = TODO("take the top element, null its slot, and halve the array when it's a quarter full")
}
