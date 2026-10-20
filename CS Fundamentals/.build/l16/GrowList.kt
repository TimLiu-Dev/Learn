class GrowList<T> {
    private var data = arrayOfNulls<Any?>(2)
    var size = 0
        private set
    val capacity: Int get() = data.size
    var copies = 0L                                   // instrumentation: elements copied so far
        private set

    fun add(element: T) {
        if (size == data.size) {                      // full: double the capacity
            data = data.copyOf(data.size * 2)         // new array + copy every element
            copies += size
        }
        data[size++] = element
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(index: Int): T {                 // list[i] calls this
        if (index !in 0 until size) throw IndexOutOfBoundsException("Index $index out of bounds for length $size")
        return data[index] as T
    }

    override fun toString() = (0 until size).joinToString(", ", "[", "]") { "${data[it]}" }
}

fun main() {
    val list = GrowList<Int>()
    var lastCap = list.capacity
    for (i in 1..20) {
        list.add(i * i)
        if (list.capacity != lastCap) {
            println("add #$i: capacity $lastCap -> ${list.capacity}")
            lastCap = list.capacity
        }
    }
    println(list)
    println("size=${list.size} capacity=${list.capacity} copies=${list.copies}")
    println(list[19])
    println(list[20])
}
