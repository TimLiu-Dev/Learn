class CountingSet<E>(private val inner: MutableSet<E> = HashSet()) : MutableSet<E> by inner {
    var addCount = 0
        private set

    override fun add(element: E): Boolean = TODO("count the attempt, then let inner do the work")

    override fun addAll(elements: Collection<E>): Boolean = TODO("count every element, then let inner do the work")
}
