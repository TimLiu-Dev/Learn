data class Hit(val addresses: List<String>, val ttlLeft: Long)

class DnsCache(private val clock: () -> Long, private val fixedTtl: Long? = null) {
    private class Entry(val addresses: List<String>, val expiresAt: Long)
    private val entries = HashMap<String, Entry>()

    fun store(name: String, addresses: List<String>, ttl: Long) {
        // Store the deadline, not the TTL: then "seconds left" is one subtraction.
        entries[name] = Entry(addresses, clock() + (fixedTtl ?: ttl))
    }

    fun lookup(name: String): Hit? {
        val entry = entries[name] ?: return null
        val left = entry.expiresAt - clock()
        if (left <= 0) {                        // at 0 seconds left the record must be discarded
            entries.remove(name)
            return null
        }
        return Hit(entry.addresses, left)
    }
}
