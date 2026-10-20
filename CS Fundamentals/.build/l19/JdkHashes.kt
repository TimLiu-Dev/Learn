import java.util.TreeMap

fun main() {
    val words = listOf("pear", "apple", "fig", "kiwi", "banana", "cherry")

    val hash = HashMap<String, Int>()
    val linked = mutableMapOf<String, Int>()     // Kotlin's default MutableMap
    val tree = TreeMap<String, Int>()
    for (w in words) { hash[w] = w.length; linked[w] = w.length; tree[w] = w.length }
    println("HashMap:       ${hash.keys}")
    println("mutableMapOf:  ${linked.keys}")
    println("TreeMap:       ${tree.keys}")

    for (i in 1..20) hash["extra$i"] = i         // grows past 12 entries: resize to 32
    for (i in 1..20) hash.remove("extra$i")
    println("HashMap again: ${hash.keys}")       // same keys, different order

    println(mutableMapOf<String, Int>().javaClass.name)
    println(hashMapOf<String, Int>().javaClass.name)
    println(mutableSetOf<String>().javaClass.name)
}
