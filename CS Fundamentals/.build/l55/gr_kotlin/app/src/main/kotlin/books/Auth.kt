package books

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// Who you are (passwords, sessions) and the CSRF token that proves a request came from our own page.
class Auth(
    passwords: Map<String, String>,                          // name -> password, hashed on the way in
    private val admins: Set<String> = emptySet(),
    private val now: () -> Long = System::currentTimeMillis,
    private val idleMs: Long = 15 * 60_000L,
    private val absoluteMs: Long = 8 * 3_600_000L,
) {
    class Session(val user: String, val admin: Boolean, val csrf: String, val created: Long, @Volatile var lastSeen: Long)

    private class Hash(val salt: ByteArray, val hash: ByteArray)
    private val rng = SecureRandom()
    private val users: Map<String, Hash> = passwords.mapValues { (_, pw) -> hashOf(pw) }
    private val dummy = hashOf("no such user")               // so unknown names cost the same as wrong passwords
    private val sessions = ConcurrentHashMap<String, Session>()

    private fun pbkdf2(pw: String, salt: ByteArray): ByteArray =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(PBEKeySpec(pw.toCharArray(), salt, 600_000, 256)).encoded
    private fun hashOf(pw: String) = ByteArray(16).also { rng.nextBytes(it) }.let { Hash(it, pbkdf2(pw, it)) }
    private fun random() = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also { rng.nextBytes(it) })

    /** A fresh session id on every successful login (no fixation), or null. */
    fun login(name: String, password: String): Pair<String, Session>? {
        val stored = users[name]
        val matches = MessageDigest.isEqual(pbkdf2(password, (stored ?: dummy).salt), (stored ?: dummy).hash)
        if (stored == null || !matches) return null
        val t = now()
        val session = Session(name, name in admins, random(), t, t)
        val id = random()
        sessions[id] = session
        return id to session
    }

    fun session(id: String?): Session? {
        val s = id?.let { sessions[it] } ?: return null
        val t = now()
        if (t - s.lastSeen >= idleMs || t - s.created >= absoluteMs) {
            sessions.remove(id)
            return null
        }
        s.lastSeen = t
        return s
    }

    fun logout(id: String?) { id?.let { sessions.remove(it) } }

    fun csrfOk(s: Session, presented: String?): Boolean =
        presented != null && MessageDigest.isEqual(s.csrf.toByteArray(), presented.toByteArray())

    companion object {
        const val COOKIE = "__Host-sid"
        fun cookie(id: String) = "$COOKIE=$id; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=28800"
        const val CLEAR = "$COOKIE=; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=0"
    }
}
