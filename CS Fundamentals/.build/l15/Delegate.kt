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

// The gotcha: override add() only, and the delegate's addAll() never calls it
class AddOnlyCountingSet<E>(private val inner: MutableSet<E> = HashSet()) : MutableSet<E> by inner {
    var addCount = 0
        private set

    override fun add(element: E): Boolean {
        addCount++
        return inner.add(element)
    }
}

fun describe(s: Set<String>) = "size ${s.size}, has Pop: ${"Pop" in s}"   // takes ANY Set

fun main() {
    val c = CountingSet<String>()
    c.addAll(listOf("Snap", "Crackle", "Pop"))
    c.add("Pop")
    println("CountingSet: counted ${c.addCount}, ${describe(c)}")

    val g = AddOnlyCountingSet<String>()
    g.add("Snap")
    g.addAll(listOf("Crackle", "Pop"))
    println("AddOnlyCountingSet: counted ${g.addCount}, ${describe(g)}")
}
