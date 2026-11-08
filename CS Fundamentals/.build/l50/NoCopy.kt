// The classic backtracking bug: storing the shared, still-changing list instead of a copy.
fun main() {
    val out = mutableListOf<List<Int>>()
    val current = mutableListOf<Int>()
    val used = BooleanArray(3)
    fun go() {
        if (current.size == 3) { out += current; return }   // BUG: should be current.toList()
        for (i in 0 until 3) {
            if (used[i]) continue
            used[i] = true; current += i + 1
            go()
            current.removeLast(); used[i] = false
        }
    }
    go()
    println(out)
    println("all the same object: ${out.all { it === current }}")
}
