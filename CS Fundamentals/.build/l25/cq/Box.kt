class Box {
    var size = 1
    val label = "box"
    fun grow() { size++ }
}

fun main() {
    println(Box::class.java.declaredMethods.map { it.name }.sorted())
}
