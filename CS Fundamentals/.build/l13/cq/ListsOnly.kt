fun main() {
    val mixed: List<Any> = listOf(listOf("a"), listOf(1, 2), "b")
    val lists = mixed.filterIsInstance<List<String>>()
    println(lists)
}
