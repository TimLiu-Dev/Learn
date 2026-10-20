import java.util.PriorityQueue

fun main() {
    val pq = PriorityQueue<Int>()                    // a min-heap by natural order
    for (x in listOf(5, 1, 8, 3, 2, 9)) pq.add(x)
    println("println(pq): $pq")                      // the backing ARRAY, not sorted order
    println("peek:        ${pq.peek()}")
    print("poll order:  ")
    while (pq.isNotEmpty()) print("${pq.poll()} ")
    println()

    // Max-heap: reverse the comparison.
    val maxPq = PriorityQueue<Int>(Comparator.reverseOrder())
    maxPq.addAll(listOf(5, 1, 8, 3, 2, 9))
    println("max first:   ${maxPq.poll()} ${maxPq.poll()} ${maxPq.poll()}")

    // Any ordering you can write as a comparator: longest word first.
    val byLength = PriorityQueue(compareByDescending<String> { it.length }.thenBy { it })
    byLength.addAll(listOf("fig", "banana", "kiwi", "cherry"))
    println("longest:     ${byLength.poll()} ${byLength.poll()}")
}
