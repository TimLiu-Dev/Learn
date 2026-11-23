// A lease-based lock, a client that pauses, and storage with and without fencing tokens.
// Virtual time: "now" is a number passed in, so nothing sleeps and every run is identical.

class LockService(private val ttlMs: Long) {
    private var holder: String? = null
    private var expiresAt = 0L
    private var nextToken = 33L                      // grows by one on every grant, never reused

    // A token if the lease is free (or has expired), null if someone else holds it.
    fun acquire(client: String, now: Long): Long? {
        if (holder != null && now < expiresAt) return null
        holder = client
        expiresAt = now + ttlMs
        return nextToken++
    }

    fun holds(client: String, now: Long) = holder == client && now < expiresAt
}

class Storage(private val fenced: Boolean) {
    var value = "(empty)"
        private set
    private var highestToken = 0L

    fun write(client: String, token: Long, newValue: String, now: Long) {
        if (fenced && token < highestToken) {
            println("  t=%5d  storage REJECTS %s: token %d < %d already seen".format(now, client, token, highestToken))
            return
        }
        highestToken = maxOf(highestToken, token)
        value = newValue
        println("  t=%5d  storage accepts %s's write (token %d): %s".format(now, client, token, newValue))
    }
}

fun scenario(fenced: Boolean) {
    println(if (fenced) "WITH fencing tokens" else "WITHOUT fencing tokens")
    val lock = LockService(ttlMs = 10_000)
    val storage = Storage(fenced)

    val t1 = lock.acquire("client1", now = 0)!!
    println("  t=    0  client1 acquires the lease, token $t1, valid until t=10000")
    println("  t= 1000  client1 checks the lease before writing: holds = ${lock.holds("client1", 1_000)}")
    println("  t= 1000  client1 stops for a 15 s GC pause, right after its check")
    println("  t= 9000  client2 tries: token ${lock.acquire("client2", now = 9_000)} (lease still held)")
    val t2 = lock.acquire("client2", now = 11_000)!!
    println("  t=11000  client2 acquires the expired lease, token $t2")
    storage.write("client2", t2, "report v2 (client2)", now = 12_000)
    // client1 wakes up. Its check said "holds = true", so it writes what it prepared before the pause.
    storage.write("client1", t1, "report v1 (client1, stale)", now = 16_000)
    println("  final value: ${storage.value}")
    println()
}

fun main() {
    scenario(fenced = false)
    scenario(fenced = true)
}
