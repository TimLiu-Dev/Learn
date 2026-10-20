import java.util.Arrays

fun describe(r: Int): String =
    if (r >= 0) "found at $r" else "missing, insert at ${-r - 1}"   // decode -(ip) - 1

fun main() {
    val a = intArrayOf(10, 20, 30, 40, 50)
    for (key in listOf(30, 35, 5, 99)) {
        val r = Arrays.binarySearch(a, key)
        println("$key -> $r  (${describe(r)})")
    }
    val words = listOf("apple", "fig", "kiwi", "pear")
    println("kiwi -> ${words.binarySearch("kiwi")}, grape -> ${words.binarySearch("grape")}")
    val unsorted = intArrayOf(50, 10, 40, 20, 30)
    println("unsorted, 10 -> ${Arrays.binarySearch(unsorted, 10)}")    // garbage in, garbage out
}
