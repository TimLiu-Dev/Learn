// build needs jar and test; both need classes. A diamond, not a cycle.
val needs = mapOf(
    "build" to listOf("jar", "test"),
    "jar" to listOf("classes"),
    "test" to listOf("classes"),
    "classes" to listOf(),
)

// Naive: "seen this vertex before?" means a cycle.
fun naive(): Boolean {
    val seen = mutableSetOf<String>()
    fun visit(v: String): Boolean {
        if (!seen.add(v)) return true
        return needs.getValue(v).any { visit(it) }
    }
    return visit("build")
}

// Three colours: only an edge back to a GREY vertex (still on the path) is a cycle.
fun colours(): Boolean {
    val grey = mutableSetOf<String>()
    val black = mutableSetOf<String>()
    fun visit(v: String): Boolean {
        if (v in grey) return true
        if (v in black) return false
        grey += v
        val found = needs.getValue(v).any { visit(it) }
        grey -= v
        black += v
        return found
    }
    return visit("build")
}

fun main() {
    println("naive=${naive()} colours=${colours()}")
}
