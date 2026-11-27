var queries = 0

fun loadFromDb(code: String): String? {
    queries++
    return if (code == "docs") "https://example.com/docs" else null
}

fun main() {
    val cache = HashMap<String, String?>()
    repeat(3) { cache.getOrPut("docs") { loadFromDb("docs") } }
    repeat(3) { cache.getOrPut("nope") { loadFromDb("nope") } }
    println("queries=$queries, entries=${cache.size}")
}
