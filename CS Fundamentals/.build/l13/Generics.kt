class Box<T>(var item: T)

class Labeled<A, B>(val label: A, val value: B) {        // two type parameters
    fun <C> withValue(newValue: C): Labeled<A, C> = Labeled(label, newValue)
    override fun toString() = "$label=$value"
}

fun <T> firstOrNull(items: List<T>): T? =                 // a generic function
    if (items.isEmpty()) null else items[0]

fun <T> repeatItem(item: T, times: Int): List<T> {
    val out = mutableListOf<T>()
    repeat(times) { out.add(item) }
    return out
}

fun main() {
    val b = Box("hi")                          // T inferred as String
    val n = Box(7)                             // T inferred as Int
    val explicit = Box<Number>(7)              // or say it yourself
    println("${b.item.length} ${n.item + 1} ${explicit.item}")

    val price = Labeled("price", 250)          // Labeled<String, Int>
    val shown = price.withValue("$2.50")       // Labeled<String, String>
    println("$price $shown")

    println(firstOrNull(listOf("a", "b")))     // T = String
    println(firstOrNull(emptyList<Int>()))     // T = Int, returns null
    println(repeatItem('x', 3))                // T = Char
}
