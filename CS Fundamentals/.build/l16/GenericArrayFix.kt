class Holder<T>(capacity: Int) {
    private val data = arrayOfNulls<Any?>(capacity)   // an Array<Any?>: fine, no T needed

    fun put(i: Int, value: T) { data[i] = value }

    @Suppress("UNCHECKED_CAST")
    fun take(i: Int): T = data[i] as T                // safe: only put() writes, and it takes a T
}

fun main() {
    val h = Holder<String>(4)
    h.put(0, "hi")
    println(h.take(0).uppercase())
}
