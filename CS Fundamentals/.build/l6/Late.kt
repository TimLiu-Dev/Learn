class Report {
    lateinit var title: String        // "I promise to set this before reading it"

    fun print() = println("Report: $title")
}

fun main() {
    val r = Report()
    try {
        r.print()
    } catch (e: UninitializedPropertyAccessException) {
        println("oops: ${e.message}")
    }
    r.title = "Q3 sales"
    r.print()
}
