import java.util.TreeSet

fun printAll(items: Collection<String>) {      // accepts ANY collection
    for (item in items) print("$item ")
    println()
}

fun main() {
    val names: MutableList<String> = ArrayList()           // declared as the interface
    names += listOf("pear", "apple", "pear")
    printAll(names)
    val unique: MutableSet<String> = TreeSet(names)        // swap the implementation freely
    printAll(unique)
}
