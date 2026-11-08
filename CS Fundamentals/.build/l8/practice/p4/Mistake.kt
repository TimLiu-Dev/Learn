class CountingSet<E>(private val inner: MutableSet<E> = HashSet()) : MutableSet<E> by inner {
    var addCount = 0
        private set

    override fun add(element: E): Boolean {
        addCount++
        return inner.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        addCount += elements.size
        var changed = false
        elements.forEach { if (add(it)) changed = true }
        return changed
    }
}
