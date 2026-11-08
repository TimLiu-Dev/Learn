class CountingSet<E>(private val inner: MutableSet<E> = HashSet()) : MutableSet<E> by inner {
    var addCount = 0
        private set

    override fun add(element: E): Boolean {
        addCount++
        return inner.add(element)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        addCount += elements.size
        return inner.addAll(elements)      // inner's addAll calls inner's add, never ours: no double count
    }
}
