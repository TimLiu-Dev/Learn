sealed interface Match {
    data class Found(val name: String, val params: Map<String, String>) : Match
    data class WrongMethod(val allowed: List<String>) : Match
    data object NoRoute : Match
}

class Route(val method: String, val template: String, val name: String)

fun segments(path: String): List<String> = path.split('/').filter { it.isNotEmpty() }

// "/books/{id}" against "/books/7" gives {id=7}; null when the shapes differ.
fun bind(template: String, path: String): Map<String, String>? =
    TODO("compare the segments pairwise: {name} binds, anything else must be equal")

fun match(routes: List<Route>, method: String, path: String): Match =
    TODO("bind every route first, then look at the methods")
