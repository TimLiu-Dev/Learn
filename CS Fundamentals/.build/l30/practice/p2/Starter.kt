data class Hit(val addresses: List<String>, val ttlLeft: Long)

// clock() is the time in seconds. A non-null fixedTtl ignores each record's own TTL, like the JVM.
class DnsCache(private val clock: () -> Long, private val fixedTtl: Long? = null) {

    fun store(name: String, addresses: List<String>, ttl: Long) {
        TODO("remember the addresses and when they stop being valid")
    }

    fun lookup(name: String): Hit? = TODO("null if unknown or expired, otherwise the addresses and seconds left")
}
