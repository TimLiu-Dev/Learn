import java.time.Instant

data class Cert(val subject: String, val issuer: String, val names: List<String>, val notAfter: Instant)

// "*.example.com" -> ".example.com", then endsWith: lets the wildcard cover any number of labels.
fun covers(name: String, host: String): Boolean {
    val n = name.lowercase()
    val h = host.lowercase()
    return if (n.startsWith("*.")) h.endsWith(n.removePrefix("*")) else n == h
}

fun verify(chain: List<Cert>, host: String, now: Instant, trusted: Set<String>): String {
    if (chain.first().names.none { covers(it, host) }) return "name mismatch"
    chain.firstOrNull { it.notAfter < now }?.let { return "expired: ${it.subject}" }
    chain.zipWithNext().forEachIndexed { i, (cert, parent) ->
        if (cert.issuer != parent.subject) return "broken chain at $i"
    }
    return if (chain.last().issuer in trusted) "OK" else "untrusted root"
}
