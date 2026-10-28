package cache

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.Executors

/** GET /books/{id}, PUT /books/{id} (body = new title), GET /stats. */
fun startServer(catalog: BookCatalog, stats: () -> String): HttpServer {
    val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    server.createContext("/books/") { ex ->
        val id = ex.requestURI.path.removePrefix("/books/").toIntOrNull()
        when {
            id == null -> send(ex, 400, """{"error":"bad id"}""")
            ex.requestMethod == "GET" -> {
                val book = catalog.find(id)
                if (book == null) send(ex, 404, """{"error":"no book $id"}""")
                else send(ex, 200, """{"id":${book.id},"title":"${json(book.title)}"}""")
            }
            ex.requestMethod == "PUT" -> {
                val title = ex.requestBody.readAllBytes().decodeToString().trim()
                catalog.save(Book(id, title))
                send(ex, 204, null)
            }
            else -> send(ex, 405, """{"error":"use GET or PUT"}""")
        }
    }
    server.createContext("/stats") { ex -> send(ex, 200, stats()) }
    server.executor = Executors.newVirtualThreadPerTaskExecutor()       // L28
    server.start()
    return server
}

private fun send(ex: HttpExchange, status: Int, body: String?) {
    if (body == null) { ex.sendResponseHeaders(status, -1); ex.close(); return }
    val bytes = body.toByteArray()
    ex.responseHeaders.add("Content-Type", "application/json")
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

private fun json(s: String) = s.replace("\\", "\\\\").replace("\"", "\\\"")
