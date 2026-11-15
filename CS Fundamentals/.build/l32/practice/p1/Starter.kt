// s as a JSON string literal, quotes included
fun quote(s: String): String = "\"" + s + "\""    // fine for "Dune"... and nothing harder

// null, Boolean, Int, Long, String, List<*> and Map<String, *> -> compact JSON text
fun encode(v: Any?): String = when (v) {
    null -> "null"
    is Boolean, is Int, is Long -> v.toString()
    is String -> quote(v)
    is List<*> -> TODO("encode each element, join with commas, wrap in [ ]")
    is Map<*, *> -> TODO("\"key\":value pairs, joined with commas, wrapped in { }")
    else -> throw IllegalArgumentException("can't encode a ${v::class.simpleName}")
}
