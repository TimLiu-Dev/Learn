import java.util.Collections

// A synchronized list stops the crash and the lost elements, but not the shuffled order.
fun squares(nums: List<Int>): List<Int> {
    val out = Collections.synchronizedList(ArrayList<Int>())
    nums.parallelStream().map { it * it }.forEach { out.add(it) }
    return out
}

fun balance(opening: Long, deposits: List<Long>): Long =
    opening + deposits.parallelStream().reduce(0L) { a, b -> a + b }

fun countLong(words: List<String>): Int =
    words.parallelStream().filter { it.length > 5 }.count().toInt()
