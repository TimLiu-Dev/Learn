data class Response(val status: Int, val body: String = "", val allow: String? = null)

class Router {
    private class Route(val method: String, val pattern: String, val handler: (Map<String, String>) -> Response)
    private val routes = mutableListOf<Route>()

    fun add(method: String, pattern: String, handler: (Map<String, String>) -> Response) {
        routes += Route(method, pattern, handler)
    }

    fun handle(method: String, path: String): Response {
        val fits = routes.filter { match(it.pattern, path) != null }
        if (fits.isEmpty()) return Response(404, "no route for $path")     // the resource doesn't exist
        val route = fits.firstOrNull { it.method == method }
            ?: return Response(405, "$method is not supported here",         // it exists, wrong verb
                allow = fits.map { it.method }.distinct().joinToString(", "))
        return route.handler(match(route.pattern, path)!!)
    }
}

// "/books/{id}" + "/books/42" -> {id=42}; "/books" + "/books" -> {}; no fit -> null
fun match(pattern: String, path: String): Map<String, String>? {
    val want = pattern.split("/")
    val got = path.split("/")
    if (want.size != got.size) return null                  // /books/42/extra is not /books/{id}
    val params = mutableMapOf<String, String>()
    for ((w, g) in want.zip(got)) {
        when {
            w.startsWith("{") && w.endsWith("}") ->
                if (g.isEmpty()) return null else params[w.removeSurrounding("{", "}")] = g
            w != g -> return null                               // literal segments must be equal
        }
    }
    return params
}
