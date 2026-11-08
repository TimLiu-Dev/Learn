enum class Size { SMALL, LARGE, MEDIUM }    // MEDIUM was added later, at the end

fun main() {
    val sorted = listOf(Size.MEDIUM, Size.LARGE, Size.SMALL).sorted()
    println("$sorted ${Size.MEDIUM > Size.LARGE}")
}
