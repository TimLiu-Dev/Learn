// Stops at the first negative, so step 6's message lists only one of them.
fun add(numbers: String): Int {
    if (numbers.isEmpty()) return 0
    var sum = 0
    for (part in numbers.split(",", "\n")) {
        val n = part.toInt()
        require(n >= 0) { "negatives not allowed: $n" }
        sum += n
    }
    return sum
}
