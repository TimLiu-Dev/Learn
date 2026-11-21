fun matches(template: String, path: String): Boolean {
    val t = template.split("/")
    val p = path.split("/")
    return t.size == p.size && t.zip(p).all { (a, b) -> a.startsWith("{") || a == b }
}

fun variables(template: String) = template.count { it == '{' }

fun main() {
    val routes = listOf("/books/{id}" to "byId", "/books/featured" to "featured")
    val path = "/books/featured"
    val first = routes.first { matches(it.first, path) }.second                        // Shelf's rule
    val ranked = routes.filter { matches(it.first, path) }.minBy { variables(it.first) }.second   // Spring's rule
    println("$first $ranked")
}
