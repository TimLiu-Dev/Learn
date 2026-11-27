// Each function must match a sequential loop and stay parallel.

// The squares of nums, in the same order as nums.
// toList() collects per chunk and joins the chunks in encounter order: no shared list.
fun squares(nums: List<Int>): List<Int> =
    nums.parallelStream().map { it * it }.toList()

// opening plus every deposit.
// The identity must be a true identity (0 for +): each chunk starts from it.
fun balance(opening: Long, deposits: List<Long>): Long =
    opening + deposits.parallelStream().reduce(0L) { a, b -> a + b }

// How many words have more than 5 letters.
// count() is a reduction, so no thread writes to a shared variable.
fun countLong(words: List<String>): Int =
    words.parallelStream().filter { it.length > 5 }.count().toInt()
