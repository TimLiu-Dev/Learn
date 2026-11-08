import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap

// Idle and absolute timeouts, with the clock passed in so a test can move time instead of waiting for it.
class SessionStore(
    private val idleMs: Long = 15 * 60_000L,                  // OWASP: 15-30 min idle for low-risk apps
    private val absoluteMs: Long = 8 * 3_600_000L,            // and 4-8 h absolute for a working day
    private val now: () -> Long = System::currentTimeMillis,
) {
    private class Session(val user: String, val created: Long, var lastSeen: Long)
    private val sessions = ConcurrentHashMap<String, Session>()
    private val rng = SecureRandom()

    fun create(user: String): String {
        val id = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also { rng.nextBytes(it) })
        val t = now()
        sessions[id] = Session(user, t, t)
        return id
    }

    fun lookup(id: String): String? {
        val s = sessions[id] ?: return null
        val t = now()
        if (t - s.lastSeen >= idleMs || t - s.created >= absoluteMs) {
            sessions.remove(id)                               // expired: forget it on the server, not just the client
            return null
        }
        s.lastSeen = t
        return s.user
    }

    fun size() = sessions.size
}

fun main() {
    var clock = 0L                                            // a fake clock, in milliseconds
    val store = SessionStore(now = { clock })
    fun at(minutes: Long, id: String) {
        clock = minutes * 60_000
        println("t=%3d min  lookup -> %s".format(minutes, store.lookup(id)))
    }
    val a = store.create("ana")
    at(10, a)                                                 // active
    at(24, a)                                                 // 14 min idle: still fine
    at(40, a)                                                 // 16 min idle: gone
    clock = 0
    val b = store.create("ben")
    for (m in 10L..470L step 10) { clock = m * 60_000; store.lookup(b) }  // busy all day, never idle
    at(470, b)
    at(480, b)                                                // 8 h after login: gone, however busy
    println("sessions left: ${store.size()}")
}
