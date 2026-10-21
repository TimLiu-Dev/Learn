sealed interface Parsed
data class PageParams(val limit: Int, val offset: Int) : Parsed
data class Invalid(val errors: List<String>) : Parsed

fun queryMap(query: String?): Map<String, String> =
    query.orEmpty().split('&').filter { '=' in it }.associate { it.substringBefore('=') to it.substringAfter('=') }

fun parsePage(query: String?): Parsed {
    val q = queryMap(query)
    val errors = mutableListOf<String>()

    fun intParam(name: String, default: Int, rule: String, ok: (Int) -> Boolean): Int {
        val raw = q[name] ?: return default             // absent: use the default
        val n = raw.toIntOrNull()
        if (n != null && ok(n)) return n
        errors += "$name must be $rule, got '$raw'"     // present but bad: remember why
        return default
    }

    val limit = intParam("limit", 10, "1..100") { it in 1..100 }
    val offset = intParam("offset", 0, ">= 0") { it >= 0 }
    return if (errors.isEmpty()) PageParams(limit, offset) else Invalid(errors)
}

fun main() {
    for (q in listOf(null, "limit=5", "limit=5&offset=20", "limit=500", "limit=two&offset=-1", "sort=title")) {
        println("%-22s -> %s".format(q, parsePage(q)))
    }
}
