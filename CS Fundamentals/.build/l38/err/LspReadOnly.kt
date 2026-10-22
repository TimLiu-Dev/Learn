fun addDefaults(tags: MutableList<String>) {
    tags += "new"
}

fun main() = addDefaults(listOf("sci-fi"))
