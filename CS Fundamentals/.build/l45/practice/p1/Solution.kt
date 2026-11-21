sealed interface Match {
    data class Found(val name: String, val params: Map<String, String>) : Match
    data class WrongMethod(val allowed: List<String>) : Match
    data object NoRoute : Match
}

class Route(val method: String, val template: String, val name: String)

fun segments(path: String): List<String> = path.split('/').filter { it.isNotEmpty() }

// "/books/{id}" against "/books/7" gives {id=7}; null when the shapes differ.
fun bind(template: String, path: String): Map<String, String>? {
    val t = segments(template)
    val p = segments(path)
    if (t.size != p.size) return null
    val params = mutableMapOf<String, String>()
    for ((ts, ps) in t.zip(p)) {
        if (ts.startsWith('{') && ts.endsWith('}')) params[ts.substring(1, ts.length - 1)] = ps
        else if (ts != ps) return null
    }
    return params
}

fun match(routes: List<Route>, method: String, path: String): Match {
    // Match the path first, for every method: that's what separates 405 from 404.
    val hits = routes.mapNotNull { r -> bind(r.template, path)?.let { r to it } }
    if (hits.isEmpty()) return Match.NoRoute
    val (route, params) = hits.firstOrNull { it.first.method == method }
        ?: return Match.WrongMethod(hits.map { it.first.method }.distinct().sorted())
    return Match.Found(route.name, params)
}
