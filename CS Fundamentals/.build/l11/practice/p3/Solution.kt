fun add(numbers: String): Int {
    if (numbers.isEmpty()) return 0                          // step 1
    val values = numbers.split(",", "\n").map { it.toInt() } // steps 2-5
    val negatives = values.filter { it < 0 }                 // step 6
    require(negatives.isEmpty()) { "negatives not allowed: ${negatives.joinToString(", ")}" }
    return values.sum()
}
