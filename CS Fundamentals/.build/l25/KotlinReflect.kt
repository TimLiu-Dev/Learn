import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

annotation class Label(val text: String)

class Book(@property:Label("Title") val title: String, val pages: Int) {
    fun summary() = "$title ($pages pages)"
}

fun main() {
    val k = Book::class
    val book = Book("Dune", 412)
    println("simpleName works without kotlin-reflect: ${k.simpleName}")
    // Everything below lives in kotlin-reflect.jar.
    for (p in k.memberProperties.sortedBy { it.name }) {
        val label = p.findAnnotation<Label>()?.text ?: "-"
        println("${p.name}: ${p.returnType} = ${p.get(book)}  label=$label")
    }
    val summary = k.declaredFunctions.first { it.name == "summary" }
    println(summary.call(book))
}
