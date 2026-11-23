class FencedStore {
    private val values = HashMap<String, String>()
    private var highest = 0L

    // One highest token for the whole store, not one per key.
    fun write(key: String, value: String, token: Long): Boolean {
        if (token < highest) return false
        highest = token
        values[key] = value
        return true
    }

    fun read(key: String): String? = values[key]
}
