import java.util.PriorityQueue

// The k largest values using a min-heap that never holds more than k items.
// The heap's root is the SMALLEST of the current top k: the one to evict.
fun topK(nums: List<Int>, k: Int): List<Int> {
    val heap = PriorityQueue<Int>()
    for (x in nums) {
        heap.add(x)
        if (heap.size > k) heap.poll()               // drop the smallest: O(log k)
    }
    return heap.sortedDescending()
}

fun main() {
    val scores = listOf(42, 7, 99, 15, 63, 88, 23, 71, 5, 94)
    println("top 3: ${topK(scores, 3)}")
    println("top 1: ${topK(scores, 1)}")
    println("top 20 of 10: ${topK(scores, 20)}")
}
