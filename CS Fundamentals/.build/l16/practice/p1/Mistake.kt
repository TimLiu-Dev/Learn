fun hasPairSum(xs: List<Int>, target: Int): Boolean {
    val all = xs.toHashSet()                  // every element, including x itself
    return xs.any { target - it in all }
}
