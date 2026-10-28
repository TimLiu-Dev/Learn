import java.util.TreeMap

// Exercise 2: the L40 bug (a cache key that ignores the query), then a canonical key:
// the same parameters in any order share one entry; different values never do.
var dbCalls = 0

fun searchDb(page: Int, size: Int): List<String> {          // the slow query
    dbCalls++
    return List(size) { i -> "Book ${(page - 1) * size + i + 1}" }
}

fun parse(query: String): Map<String, String> =
    query.split("&").associateTo(TreeMap()) { pair ->        // sorted by name
        val kv = pair.split("=", limit = 2)
        kv[0].trim().lowercase() to (kv.getOrNull(1)?.trim() ?: "")
    }

fun canonicalKey(path: String, query: String) =
    path + "?" + parse(query).entries.joinToString("&") { (k, v) -> "$k=$v" }

val cache = HashMap<String, List<String>>()

fun search(query: String, buggy: Boolean): List<String> {
    val key = if (buggy) "books" else canonicalKey("/books", query)
    return cache.getOrPut(key) {
        val p = parse(query)
        searchDb(p.getValue("page").toInt(), p.getValue("size").toInt())
    }
}

fun main() {
    val requests = listOf("page=2&size=3", "size=3&page=2", "Page=2&SIZE=3", "page=3&size=3")
    for (buggy in listOf(true, false)) {
        cache.clear(); dbCalls = 0
        println(if (buggy) "key = \"books\" (the L40 bug)" else "key = canonical path + sorted params")
        for (q in requests) println("  %-15s -> %s".format(q, search(q, buggy)))
        println("  db calls: $dbCalls, keys: ${cache.keys}")
    }
}
