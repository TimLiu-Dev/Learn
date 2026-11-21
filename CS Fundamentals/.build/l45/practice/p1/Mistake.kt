sealed interface Match {
    data class Found(val name: String, val params: Map<String, String>) : Match
    data class WrongMethod(val allowed: List<String>) : Match
    data object NoRoute : Match
}

class Route(val method: String, val template: String, val name: String)

fun segments(path: String): List<String> = path.split('/').filter { it.isNotEmpty() }

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

// Filters by method first: a known path with the wrong method becomes a 404.
fun match(routes: List<Route>, method: String, path: String): Match {
    for (r in routes.filter { it.method == method }) {
        val params = bind(r.template, path) ?: continue
        return Match.Found(r.name, params)
    }
    return Match.NoRoute
}
