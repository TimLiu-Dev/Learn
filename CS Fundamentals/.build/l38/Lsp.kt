// A function written against the base type: "a list I can add to".
fun addDefaults(tags: MutableList<String>) {
    tags += "new"
    tags += "unread"
}

fun main() {
    val tags = mutableListOf("sci-fi")
    addDefaults(tags)
    println("mutable list:   $tags")

    // addDefaults(listOf("sci-fi"))        // won't compile: a List is not a MutableList (err/LspReadOnly.kt)

    val sneaky = listOf("sci-fi") as MutableList<String>   // a cast forces it through...
    try {
        addDefaults(sneaky)
    } catch (e: UnsupportedOperationException) {
        println("cast loophole:  $e")                   // ...and the substitution fails at runtime
    }
}
