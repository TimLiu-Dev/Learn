class Server {
    var host = "localhost"
    var port = 80
    var tls = false
    override fun toString() = "Server($host:$port, tls=$tls)"
}

fun findNickname(name: String): String? = if (name == "Margaret") "Peggy" else null

fun main() {
    // let: run a block only when non-null; `it` is the value; returns the block's result
    println("let: ${findNickname("Margaret")?.let { it.length }}")
    println("let on null: ${findNickname("Ada")?.let { it.length }}")

    // apply: configure an object; `this` is the object; returns the object
    val server = Server().apply {
        host = "example.com"
        port = 443
        tls = true
    }
    println("apply: $server")

    // also: a side effect in the middle of a chain; `it`; returns the object unchanged
    val ports = listOf(8080, 80, 443)
        .also { println("also: sorting $it") }
        .sorted()
    println("sorted: $ports")

    // run: compute a value from an object's members; `this`; returns the block's result
    val url = server.run { (if (tls) "https" else "http") + "://$host:$port" }
    println("run: $url")

    // with: group calls on an object you already have; a plain function, not an extension
    val summary = with(StringBuilder()) {
        append("ports=")
        append(ports.joinToString("/"))
        toString()
    }
    println("with: $summary")

    // takeIf / takeUnless: keep the value if the predicate holds (or doesn't), else null
    println("takeIf: ${server.port.takeIf { it % 2 == 0 }}, ${server.port.takeUnless { it % 2 == 0 }}")
}
