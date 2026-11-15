// Correct, but O(n²): every pair (i, j) gets checked.
fun hasPairSum(xs: List<Int>, target: Int): Boolean {
    for (i in xs.indices)
        for (j in i + 1 until xs.size)
            if (xs[i] + xs[j] == target) return true
    return false
}
