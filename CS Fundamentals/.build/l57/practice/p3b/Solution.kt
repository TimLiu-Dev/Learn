// The storage service checks fencing tokens: a write whose token has gone
// backwards comes from a lock holder whose lease has already expired.
class FencedStore {
    private val values = HashMap<String, String>()
    private val highest = HashMap<String, Long>()

    // Accept the write (and return true) unless a higher token was already accepted for this key.
    @Synchronized
    fun write(key: String, value: String, token: Long): Boolean {
        val seen = highest[key]
        if (seen != null && token < seen) return false   // stale holder: change nothing
        highest[key] = token
        values[key] = value
        return true
    }

    @Synchronized
    fun read(key: String): String? = values[key]
}
