fun verify(sizes: IntArray, target: Int, pick: List<Int>): Boolean =
    pick.all { it in sizes.indices } && pick.sumOf { sizes[it] } == target

fun search(sizes: IntArray, target: Int): List<Int>? {
    for (mask in 0 until (1 shl sizes.size)) {
        val pick = sizes.indices.filter { mask and (1 shl it) != 0 }
        if (verify(sizes, target, pick)) return pick
    }
    return null
}
