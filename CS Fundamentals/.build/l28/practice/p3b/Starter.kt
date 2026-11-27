// Each function must match a sequential loop and stay parallel.

// The squares of nums, in the same order as nums.
fun squares(nums: List<Int>): List<Int> {
    val out = ArrayList<Int>()
    nums.parallelStream().map { it * it }.forEach { out.add(it) }
    return out
}

// opening plus every deposit.
fun balance(opening: Long, deposits: List<Long>): Long =
    deposits.parallelStream().reduce(opening) { a, b -> a + b }

// How many words have more than 5 letters.
fun countLong(words: List<String>): Int {
    var n = 0
    words.parallelStream().forEach { if (it.length > 5) n++ }
    return n
}
