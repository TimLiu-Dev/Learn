fun main() {
    val title = "Dune"
    val a = title.take(10)
    val b = listOf("Ada").getOrNull(-1)
    val c = runCatching { title.take(-1) }.getOrElse { it.javaClass.simpleName }
    println("$a $b [$c]")
}
