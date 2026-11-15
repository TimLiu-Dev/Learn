import java.util.TreeMap

fun main() {
    // The first key of each block -> where that block starts in the SSTable
    val sparse = TreeMap(mapOf("apple" to 0L, "kiwi" to 400L, "pear" to 800L))
    val keys = listOf("banana", "kiwi", "aardvark", "zebra")
    println(keys.map { sparse.floorEntry(it)?.value })
}
