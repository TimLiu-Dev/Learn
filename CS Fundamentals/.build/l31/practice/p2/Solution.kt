import java.time.Instant

data class Cert(val subject: String, val issuer: String, val names: List<String>, val notAfter: Instant)

// Does one name from a certificate cover the host? "*.example.com" covers exactly one extra label.
fun covers(name: String, host: String): Boolean {
    val n = name.lowercase()
    val h = host.lowercase()
    if (!n.startsWith("*.")) return n == h
    return h.substringAfter('.', "") == n.removePrefix("*.")   // drop the host's first label, compare the rest
}

// "OK", or the first problem found, checked in this order:
//   "name mismatch"       no name in the leaf (chain[0]) covers the host
//   "expired: SUBJECT"    the first certificate whose notAfter is before now
//   "broken chain at I"   certificate I's issuer isn't certificate I+1's subject
//   "untrusted root"      the last certificate's issuer isn't in the trust store
fun verify(chain: List<Cert>, host: String, now: Instant, trusted: Set<String>): String {
    if (chain.first().names.none { covers(it, host) }) return "name mismatch"
    chain.firstOrNull { it.notAfter < now }?.let { return "expired: ${it.subject}" }
    chain.zipWithNext().forEachIndexed { i, (cert, parent) ->
        if (cert.issuer != parent.subject) return "broken chain at $i"
    }
    return if (chain.last().issuer in trusted) "OK" else "untrusted root"
}
