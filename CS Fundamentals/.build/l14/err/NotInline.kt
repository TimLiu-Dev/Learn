fun timesDo(n: Int, action: (Int) -> Unit) {      // NOT inline
    for (i in 1..n) action(i)
}

fun firstOver(limit: Int): Int {
    timesDo(10) { i -> if (i * i > limit) return i }
    return -1
}
