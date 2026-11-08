import java.security.SecureRandom
import java.util.Base64

// Refresh token rotation (RFC 9700 §4.14.2): every refresh returns a NEW refresh token and retires the old one.
// If a retired token ever comes back, two parties hold copies, so the whole family is revoked.
class TokenService {
    private class Rt(val family: Int, val user: String, var used: Boolean = false)
    private val tokens = HashMap<String, Rt>()
    private val revokedFamilies = HashSet<Int>()
    private var nextFamily = 1
    private val rng = SecureRandom()

    private fun random() = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also { rng.nextBytes(it) })

    @Synchronized fun login(user: String): String = random().also { tokens[it] = Rt(nextFamily++, user) }

    /** Returns (new access token, new refresh token), or an error message. */
    @Synchronized fun refresh(presented: String): Result<Pair<String, String>> {
        val rt = tokens[presented] ?: return Result.failure(IllegalArgumentException("unknown token"))
        if (rt.family in revokedFamilies) return Result.failure(IllegalStateException("family revoked: log in again"))
        if (rt.used) {
            revokedFamilies += rt.family                      // reuse detected: nobody keeps this login
            return Result.failure(SecurityException("REUSE of a retired token: family ${rt.family} revoked"))
        }
        rt.used = true
        val next = random().also { tokens[it] = Rt(rt.family, rt.user) }
        return Result.success("access(${rt.user}, 15 min)" to next)
    }
}

fun main() {
    val auth = TokenService()
    fun show(who: String, r: Result<Pair<String, String>>) =
        println("%-34s -> %s".format(who, r.fold({ "ok, ${it.first}, new refresh ${it.second.take(6)}…" }, { it.message })))

    val rt1 = auth.login("ana")
    val r2 = auth.refresh(rt1).also { show("app refreshes with rt1", it) }
    val rt2 = r2.getOrThrow().second
    show("a stolen copy of rt1 is used", auth.refresh(rt1))
    show("app refreshes with its rt2", auth.refresh(rt2))
    show("app sends a made-up token", auth.refresh("AAAA"))
    val fresh = auth.login("ana")
    show("ana logs in again, new family", auth.refresh(fresh))
}
