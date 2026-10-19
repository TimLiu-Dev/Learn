class Counter(var count: Int)

fun bumpInt(n: Int): Int {
    // n = n + 1          // won't compile: parameters are read-only
    var local = n
    local++
    return local
}

fun bump(c: Counter) {
    c.count++              // follows the copied arrow to the SAME object
}

fun replace(c: Counter) {
    var local = c
    local = Counter(100)   // re-points only the local copy
}

fun main() {
    var n = 5
    bumpInt(n)
    println("n=$n")

    val counter = Counter(5)
    bump(counter)
    println("after bump: ${counter.count}")
    replace(counter)
    println("after replace: ${counter.count}")
}
