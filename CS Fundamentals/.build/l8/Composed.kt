class InstrumentedSet<E>(private val inner: MutableSet<E> = HashSet()) {   // HAS a set
    var addCount = 0
        private set

    fun add(element: E): Boolean {
        addCount++
        return inner.add(element)                  // forward the real work
    }

    fun addAll(elements: Collection<E>): Boolean {
        addCount += elements.size
        return inner.addAll(elements)              // inner.addAll can't call OUR add
    }

    val size: Int get() = inner.size
}

// Preview of L15: Kotlin writes every other forwarding method for you with `by`
class CountingSet<E>(private val inner: MutableSet<E> = HashSet()) : MutableSet<E> by inner {
    var addCount = 0
        private set

    override fun add(element: E): Boolean {
        addCount++
        return inner.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        addCount += elements.size
        return inner.addAll(elements)
    }
}

fun main() {
    val s = InstrumentedSet<String>()
    s.addAll(listOf("Snap", "Crackle", "Pop"))
    println("added 3, counted ${s.addCount}, size ${s.size}")

    val c = CountingSet<String>()
    c.addAll(listOf("Snap", "Crackle", "Pop"))
    println("added 3, counted ${c.addCount}, contains Pop: ${"Pop" in c}")
}
