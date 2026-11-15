data class Response(val status: Int, val body: String = "", val allow: String? = null)

class Router {
    private class Route(val method: String, val pattern: String, val handler: (Map<String, String>) -> Response)
    private val routes = mutableListOf<Route>()

    fun add(method: String, pattern: String, handler: (Map<String, String>) -> Response) {
        routes += Route(method, pattern, handler)
    }

    fun handle(method: String, path: String): Response {
        for (r in routes) {
            val params = match(r.pattern, path)
            if (params != null && r.method == method) return r.handler(params)
        }
        return Response(404, "no route for $path")       // TODO: 405 when the path exists
    }
}

// "/books/{id}" + "/books/42" -> {id=42}; "/books" + "/books" -> {}; no fit -> null
fun match(pattern: String, path: String): Map<String, String>? = TODO("split both on '/' and compare segments")
