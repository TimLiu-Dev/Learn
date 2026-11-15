fun hasPairSum(xs: List<Int>, target: Int): Boolean {
    val seen = HashSet<Int>()
    for (x in xs) {
        if (target - x in seen) return true   // an earlier element completes the pair: O(1) on average
        seen.add(x)                           // add x only now, so it can't pair with itself
    }
    return false                              // one pass: O(n) time, O(n) extra space
}
