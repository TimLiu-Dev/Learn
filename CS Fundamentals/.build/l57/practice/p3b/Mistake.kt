class FencedStore {
    private val values = HashMap<String, String>()
    private val highest = HashMap<String, Long>()

    // Strictly greater: the current holder's second write is rejected too.
    fun write(key: String, value: String, token: Long): Boolean {
        val seen = highest[key]
        if (seen != null && token <= seen) return false
        highest[key] = token
        values[key] = value
        return true
    }

    fun read(key: String): String? = values[key]
}
