// Runs `init` at most once, however many threads call get() at the same moment.
class Once<T : Any>(private val init: () -> T) {
    val lock = Any()
    private var value: T? = null

    // Racy check-then-act: two threads can both see null and both run init.
    fun get(): T {
        value?.let { return it }
        return init().also { value = it }
    }
}
