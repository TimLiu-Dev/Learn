// Double-checked locking without @Volatile: usually works in testing, broken under the JMM.
class Once<T : Any>(private val init: () -> T) {
    val lock = Any()
    private var value: T? = null

    fun get(): T {
        value?.let { return it }
        synchronized(lock) {
            value?.let { return it }
            return init().also { value = it }
        }
    }
}
