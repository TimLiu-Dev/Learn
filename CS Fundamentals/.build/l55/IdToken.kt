// The client's checks on an ID token's claims (OIDC Core §3.1.3.7), after its signature was verified.
// Returns every problem, not just the first, so a log line explains the whole rejection.
fun idTokenProblems(claims: Map<String, Any?>, issuer: String, clientId: String, nonce: String, now: Long): List<String> =
    buildList {
        if (claims["iss"] != issuer) add("iss must be exactly $issuer")
        val aud = when (val a = claims["aud"]) { is String -> listOf(a); is List<*> -> a; else -> emptyList() }
        if (clientId !in aud) add("aud must contain $clientId")
        if (aud.any { it != clientId }) add("aud has audiences we don't trust")
        if (claims["nonce"] != nonce) add("nonce doesn't match the one this login sent")
        val exp = claims["exp"] as? Long
        if (exp == null || now >= exp) add("expired or no exp")
        if (claims["sub"] == null) add("no sub")
    }

fun main() {
    val iss = "https://as.example"
    val good = mapOf("iss" to iss, "sub" to "ana", "aud" to "photo-app", "nonce" to "n-123", "exp" to 2_000L)
    val cases = listOf(
        "the real one" to good,
        "for another app" to good + ("aud" to "other-app"),
        "shared with others" to good + ("aud" to listOf("photo-app", "ad-network")),
        "replayed from an old login" to good + ("nonce" to "n-999"),
        "lookalike issuer" to good + ("iss" to "https://as.example.evil"),
        "everything wrong" to mapOf("iss" to "x", "aud" to "y", "exp" to 10L),
    )
    for ((name, claims) in cases) {
        val problems = idTokenProblems(claims, iss, "photo-app", "n-123", now = 1_000)
        println("%-27s %s".format(name, if (problems.isEmpty()) "accept" else "reject: " + problems.joinToString("; ")))
    }
}
