// Exercise 1: when two templates match one path, which wins?
// Shelf's Router takes the FIRST registered match; Spring ranks by specificity instead.

fun segments(path: String) = path.split('/').filter { it.isNotEmpty() }

fun bind(template: String, path: String): Map<String, String>? {
    val t = segments(template)
    val p = segments(path)
    if (t.size != p.size) return null
    val params = mutableMapOf<String, String>()
    for ((ts, ps) in t.zip(p)) {
        if (ts.startsWith('{')) params[ts.trim('{', '}')] = ps
        else if (ts != ps) return null
    }
    return params
}

// What Shelf's Router does: registration order decides.
fun matchFirst(templates: List<String>, path: String) =
    templates.firstNotNullOfOrNull { t -> bind(t, path)?.let { t to it } }

// The fix: fewest path variables wins, like Spring's PathPattern.SPECIFICITY_COMPARATOR.
fun matchMostSpecific(templates: List<String>, path: String) =
    templates.mapNotNull { t -> bind(t, path)?.let { t to it } }.minByOrNull { it.second.size }

fun main() {
    val templateFirst = listOf("/books/{id}", "/books/count")
    val literalFirst = listOf("/books/count", "/books/{id}")
    println("first match,   template registered first: ${matchFirst(templateFirst, "/books/count")}")
    println("first match,   literal registered first:  ${matchFirst(literalFirst, "/books/count")}")
    println("most specific, template registered first: ${matchMostSpecific(templateFirst, "/books/count")}")
    println("most specific, any path still binds:      ${matchMostSpecific(templateFirst, "/books/42")}")
}
