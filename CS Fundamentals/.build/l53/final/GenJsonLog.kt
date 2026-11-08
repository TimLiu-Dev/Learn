import java.io.File

// Writes access.jsonl: one JSON object per line, the shape L46's server will log
fun main(args: Array<String>) {
    val lines = args.firstOrNull()?.toInt() ?: 200_000
    val clients = listOf("ada", "grace", "linus", "bot", "bot", "bot")
    val rnd = java.util.Random(530)                      // same seed, same bytes as GenJsonLog.java
    File("access.jsonl").bufferedWriter().use { out ->
        for (i in 0 until lines) {
            val roll = rnd.nextInt(100)
            val (method, path) = when {
                roll < 20 -> "GET" to "/books"
                roll < 55 -> "GET" to "/books/${1 + rnd.nextInt(500)}"
                roll < 60 -> "POST" to "/books"
                roll < 70 -> "GET" to "/members/${1 + rnd.nextInt(80)}"
                roll < 80 -> "POST" to "/loans"
                roll < 85 -> "POST" to "/loans/${1 + rnd.nextInt(3000)}/return"
                roll < 95 -> "GET" to "/health"
                else -> "GET" to "/metrics"
            }
            val client = clients[rnd.nextInt(clients.size)]
            val r = rnd.nextInt(1000)
            val status = when {
                r < 10 -> 500
                client == "bot" && r < 60 -> 429
                path.startsWith("/books/") && r < 110 -> 404
                path == "/loans" && r < 200 -> 409
                method == "POST" && !path.endsWith("/return") -> 201
                else -> 200
            }
            val ms = rnd.nextInt(8) + (if (path.startsWith("/books")) 10 else 2) +
                (if (rnd.nextInt(50) == 0) 200 + rnd.nextInt(1800) else 0)
            val t = i * 50L                                  // a request every 50 ms from 09:00
            val ts = "2026-11-06T%02d:%02d:%02d.%03dZ".format(9 + t / 3_600_000, t / 60_000 % 60, t / 1000 % 60, t % 1000)
            val id = "%016x".format(rnd.nextLong())
            out.write("""{"ts":"$ts","level":"INFO","requestId":"$id","method":"$method","path":"$path","status":$status,"ms":$ms,"client":"$client"}""")
            out.write("\n")
        }
    }
    println("wrote $lines lines to access.jsonl")
}
