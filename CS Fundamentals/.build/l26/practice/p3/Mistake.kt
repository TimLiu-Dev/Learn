class Memo(private val loader: (String) -> String) {
    private val lock = Any()
    private val values = HashMap<String, String>()
    var loads = 0
        get() = synchronized(lock) { field }
        private set

    fun get(key: String): String {
        val cached = synchronized(lock) { values[key] }      // the check is locked...
        if (cached != null) return cached
        val value = loader(key)                              // ...but this gap isn't
        synchronized(lock) { loads++; values[key] = value }  // ...and the act is locked again
        return value
    }
}
