// Runs `init` at most once, however many threads call get() at the same moment.
class Once<T : Any>(private val init: () -> T) {
    val lock = Any()
    @Volatile private var value: T? = null       // the fast path reads it without the lock

    fun get(): T {
        value?.let { return it }                 // 1st check, no lock: the common case once set
        synchronized(lock) {
            value?.let { return it }             // 2nd check: another thread may have won meanwhile
            return init().also { value = it }
        }
    }
}
