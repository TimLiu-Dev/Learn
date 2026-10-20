interface Source<out T> {
    fun next(): T
    fun put(item: T)                    // T in an "in" position
}

interface Sink<in T> {
    fun put(item: T)
    fun peek(): T                       // T in an "out" position
}

fun addOne(items: MutableList<*>) {
    items.add(1)                        // star projection: can't write
}
