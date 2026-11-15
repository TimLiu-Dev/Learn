// Caches loader's results. loader must run at most once per key.
class Memo(private val loader: (String) -> String) {
    private val values = HashMap<String, String>()
    var loads = 0                                // how many times loader ran
        @Synchronized get                        // reads use the same lock as writes
        private set

    // One lock (this) around the whole check-then-act, so no thread can slip in between.
    @Synchronized
    fun get(key: String): String = values.getOrPut(key) {
        loads++
        loader(key)
    }
}
