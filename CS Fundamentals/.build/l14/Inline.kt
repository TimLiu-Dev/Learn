val days = listOf("Mon", "Tue", "STOP", "Thu")

fun nonLocal() {
    days.forEach {
        if (it == "STOP") return        // returns from nonLocal() itself
        println("  $it")
    }
    println("  never printed")
}

fun labeled() {
    days.forEach {
        if (it == "STOP") return@forEach  // skips this element only, like continue
        println("  $it")
    }
    println("  end of labeled()")
}

// My own inline higher-order function: the lambda is pasted into the caller
inline fun timesDo(n: Int, action: (Int) -> Unit) {
    for (i in 1..n) action(i)
}

fun firstOver(limit: Int): Int {
    timesDo(10) { i -> if (i * i > limit) return i }  // non-local: allowed, timesDo is inline
    return -1
}

fun main() {
    println("nonLocal:"); nonLocal()
    println("labeled:"); labeled()
    println(firstOver(20))
}
