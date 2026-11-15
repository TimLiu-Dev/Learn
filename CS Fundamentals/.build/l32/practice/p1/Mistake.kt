// Escaping with a chain of replace calls, quotes first
fun quote(s: String): String = "\"" + s
    .replace("\"", "\\\"")
    .replace("\\", "\\\\")
    .replace("\n", "\\n")
    .replace("\r", "\\r")
    .replace("\t", "\\t")
    .replace("\b", "\\b")
    .replace("\u000C", "\\f")
    .replace("\u0001", "\\u0001") + "\""

fun encode(v: Any?): String = when (v) {
    null -> "null"
    is Boolean, is Int, is Long -> v.toString()
    is String -> quote(v)
    is List<*> -> v.joinToString(",", "[", "]") { encode(it) }
    is Map<*, *> -> v.entries.joinToString(",", "{", "}") { (k, x) -> quote(k as String) + ":" + encode(x) }
    else -> throw IllegalArgumentException("can't encode a ${v::class.simpleName}")
}
