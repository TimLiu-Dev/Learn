// Fail closed at startup: every route must say who may call it, or the server refuses to start.
enum class Access { PUBLIC, LOGGED_IN, OWNER_OR_ADMIN, ADMIN }

data class Route(val method: String, val path: String, val access: Access?)   // null = somebody forgot

fun checkRoutes(routes: List<Route>) {
    val unguarded = routes.filter { it.access == null }
    check(unguarded.isEmpty()) {
        "refusing to start: no access rule for " + unguarded.joinToString { "${it.method} ${it.path}" }
    }
}

fun main() {
    val routes = mutableListOf(
        Route("GET", "/books", Access.PUBLIC),
        Route("GET", "/books/{id}", Access.PUBLIC),
        Route("POST", "/books", Access.LOGGED_IN),
        Route("PUT", "/books/{id}", Access.OWNER_OR_ADMIN),
        Route("DELETE", "/books/{id}", null),                          // added in a hurry
        Route("GET", "/admin/users", null),
    )
    try {
        checkRoutes(routes)
    } catch (e: IllegalStateException) {
        println(e.message)
    }
    routes.replaceAll { if (it.access == null) it.copy(access = if (it.path.startsWith("/admin")) Access.ADMIN else Access.OWNER_OR_ADMIN) else it }
    checkRoutes(routes)
    println("all ${routes.size} routes have a rule: starting")
    routes.forEach { println("  %-6s %-13s %s".format(it.method, it.path, it.access)) }
}
