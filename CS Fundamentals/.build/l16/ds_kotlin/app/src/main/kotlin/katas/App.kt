package katas

import kotlin.time.measureTime

// A deliberately bad dynamic array: grows by a constant 10 slots instead of doubling.
class PlusTenList<T> {
    private var data = arrayOfNulls<Any?>(4)
    var size = 0
        private set

    fun add(element: T) {
        if (size == data.size) data = data.copyOf(data.size + 10)
        data[size++] = element
    }
}

fun bestOf3Ms(label: String, n: Int, addAll: (Int) -> Int) {
    addAll(n / 10)                                          // warm-up
    var best = Double.MAX_VALUE
    var size = 0
    repeat(3) {
        val d = measureTime { size = addAll(n) }            // keep the result: no dead code
        best = minOf(best, d.inWholeMicroseconds / 1e3)
    }
    println("%-20s size %,d  %9.1f ms".format(label, size, best))
}

fun main() {
    val n = 1_000_000
    println("$n adds, best of 3, on my machine:")
    bestOf3Ms("java.util.ArrayList", n) { k -> val l = ArrayList<Int>(); repeat(k) { l.add(it) }; l.size }
    bestOf3Ms("MyArrayList (x2)", n) { k -> val l = MyArrayList<Int>(); repeat(k) { l.add(it) }; l.size }
    bestOf3Ms("PlusTenList (+10)", n) { k -> val l = PlusTenList<Int>(); repeat(k) { l.add(it) }; l.size }
}
