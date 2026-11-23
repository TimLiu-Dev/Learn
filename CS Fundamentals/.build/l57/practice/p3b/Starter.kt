// The storage service checks fencing tokens: a write whose token has gone
// backwards comes from a lock holder whose lease has already expired.
class FencedStore {
    // Accept the write (and return true) unless a higher token was already accepted for this key.
    fun write(key: String, value: String, token: Long): Boolean = TODO("compare with the highest token seen for this key")

    fun read(key: String): String? = TODO("the key's current value")
}
