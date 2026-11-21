import java.time.Instant

/** What's wrong with a security.txt (RFC 9116) at [now]; empty means it's usable. */
fun problems(text: String, now: Instant): List<String> {
    val fields = text.lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && ':' in it }
        .map { it.substringBefore(':').trim().lowercase() to it.substringAfter(':').trim() }   // split at the FIRST colon only

    val out = mutableListOf<String>()
    val contacts = fields.filter { it.first == "contact" }.map { it.second }
    if (contacts.isEmpty()) out += "missing Contact"
    for (c in contacts) {
        if (listOf("mailto:", "tel:", "https://").none { c.startsWith(it) }) out += "Contact must use mailto:, tel: or https://: $c"
    }

    val expires = fields.filter { it.first == "expires" }.map { it.second }
    when {
        expires.isEmpty() -> out += "missing Expires"
        expires.size > 1 -> out += "Expires must appear once, found ${expires.size}"
        else -> {
            val at = Instant.parse(expires.single())
            if (at.isBefore(now)) out += "expired on $at"
        }
    }
    return out
}
