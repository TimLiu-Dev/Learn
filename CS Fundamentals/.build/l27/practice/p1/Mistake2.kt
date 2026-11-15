// Correct, but every call takes the lock, even long after the value exists.
class Once<T : Any>(private val init: () -> T) {
    val lock = Any()
    @Volatile private var value: T? = null

    fun get(): T = synchronized(lock) {
        value ?: init().also { value = it }
    }
}
