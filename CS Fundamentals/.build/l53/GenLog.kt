import java.io.File

// Writes requests.log in L22's format: "2026-11-06T09:00:01 GET /api/users 200 12ms"
fun main(args: Array<String>) {
    val lines = args.firstOrNull()?.toInt() ?: 1_000_000
    val paths = listOf("/health", "/api/books", "/api/books", "/api/books", "/api/login",
        "/api/members", "/api/loans", "/api/loans", "/api/search", "/metrics")
    val rnd = java.util.Random(53)                       // same seed, same bytes as GenLog.java
    File("requests.log").bufferedWriter().use { out ->
        for (i in 0 until lines) {
            val path = paths[rnd.nextInt(paths.size)]
            val method = if (path == "/api/loans" && rnd.nextInt(3) == 0) "POST" else "GET"
            val roll = rnd.nextInt(100)
            val status = when {
                roll < 2 -> 500
                roll < 5 -> 404
                method == "POST" -> 201
                else -> 200
            }
            val ms = 2 + rnd.nextInt(40) + if (rnd.nextInt(20) == 0) rnd.nextInt(900) else 0
            val s = i % 60; val m = i / 60 % 60; val h = 9 + i / 3600 % 12
            out.write("2026-11-06T%02d:%02d:%02d %s %s %d %dms\n".format(h, m, s, method, path, status, ms))
        }
    }
    println("wrote $lines lines to requests.log")
}
