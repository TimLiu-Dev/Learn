class InstrumentedHashSet<E> : HashSet<E>() {     // inherits from a class it doesn't control
    var addCount = 0
        private set

    override fun add(element: E): Boolean {
        addCount++
        return super.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        addCount += elements.size
        return super.addAll(elements)
    }
}

fun main() {
    val s = InstrumentedHashSet<String>()
    s.addAll(listOf("Snap", "Crackle", "Pop"))
    println("added 3, counted ${s.addCount}")
}
