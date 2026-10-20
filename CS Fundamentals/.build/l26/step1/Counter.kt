package demo

interface Counter {
    fun increment()
    val value: Int
}

class UnsafeCounter : Counter {
    private var count = 0
    override fun increment() { count++ }
    override val value get() = count
}
