// s as a JSON string literal, quotes included
fun quote(s: String): String = buildString {
    append('"')
    for (c in s) {
        when (c) {
            '"' -> append("\\\"")
            '\\' -> append("\\\\")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            in '\u0000'..'\u001F' -> append("\\u%04x".format(c.code))   // any other control character
            else -> append(c)                                          // everything else goes through as is
        }
    }
    append('"')
}

// null, Boolean, Int, Long, String, List<*> and Map<String, *> -> compact JSON text
fun encode(v: Any?): String = when (v) {
    null -> "null"
    is Boolean, is Int, is Long -> v.toString()
    is String -> quote(v)
    is List<*> -> v.joinToString(",", "[", "]") { encode(it) }                       // recursion handles nesting
    is Map<*, *> -> v.entries.joinToString(",", "{", "}") { (k, x) -> quote(k as String) + ":" + encode(x) }
    else -> throw IllegalArgumentException("can't encode a ${v::class.simpleName}")
}
