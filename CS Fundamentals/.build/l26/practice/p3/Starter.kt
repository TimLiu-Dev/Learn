// Caches loader's results. loader must run at most once per key.
class Memo(private val loader: (String) -> String) {
    private val values = HashMap<String, String>()
    var loads = 0                                // how many times loader ran
        private set

    fun get(key: String): String {
        val cached = values[key]
        if (cached != null) return cached        // check...
        loads++
        val value = loader(key)
        values[key] = value                      // ...then act
        return value
    }
}
