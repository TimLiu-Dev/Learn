fun main() {
    val a = ArrayList<String>()
    val b = mutableListOf<String>()
    val c = listOf("x", "y")
    println(a.javaClass.name)        // Kotlin's ArrayList on the JVM
    println(b.javaClass.name)        // what mutableListOf() gives you
    println(c.javaClass.name)        // listOf() with 2+ elements
}
