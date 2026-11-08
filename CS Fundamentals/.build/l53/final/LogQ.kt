import java.io.File

// The same five answers as logq.sh, in one pass over the file
data class Req(val method: String, val path: String, val status: Int, val ms: Int, val id: String, val client: String)

// This log's lines are flat {"key":"text" or number} pairs, so a regex is enough here.
// Real JSON (nesting, escapes) needs a parser: L32.
val FIELD = Regex(""""(\w+)":("[^"]*"|\d+)""")

fun parse(line: String): Req {
    val f = FIELD.findAll(line).associate { it.groupValues[1] to it.groupValues[2].removeSurrounding("\"") }
    return Req(f.getValue("method"), f.getValue("path"), f.getValue("status").toInt(),
        f.getValue("ms").toInt(), f.getValue("requestId"), f.getValue("client"))
}

fun main(args: Array<String>) {
    val reqs = File(args[0]).useLines { lines -> lines.map(::parse).toList() }
    println("== requests: ${reqs.size}")

    println("== by status")
    reqs.groupingBy { it.status }.eachCount().toSortedMap()
        .forEach { (status, n) -> println("%d %7d".format(status, n)) }

    println("== top 5 endpoints")
    reqs.groupingBy { "${it.method} ${it.path.replace(Regex("/[0-9]+"), "/{id}")}" }.eachCount()
        .entries.sortedByDescending { it.value }.take(5)
        .forEach { (endpoint, n) -> println("%7d %s".format(n, endpoint)) }

    println("== 5 slowest requests")
    reqs.sortedWith(compareByDescending<Req> { it.ms }.thenBy { it.id }).take(5)
        .forEach { println("%5d ms  %s %s  %s".format(it.ms, it.method, it.path, it.id)) }

    println("== p95 latency of GET /books/{id}")
    val books = reqs.filter { it.method == "GET" && it.path.matches(Regex("/books/[0-9]+")) }.map { it.ms }.sorted()
    println("${books[(books.size * 95 + 99) / 100 - 1]} ms over ${books.size} requests")   // nearest rank (L22)

    println("== rate-limited (429) by client")
    reqs.filter { it.status == 429 }.groupingBy { it.client }.eachCount()
        .entries.sortedByDescending { it.value }
        .forEach { (client, n) -> println("%7d %s".format(n, client)) }
}
