class Counter {
    var calls = 0
    fun next() = ++calls
    override fun toString() = "Counter(next=${next()})"
}

fun main() {
    val c = Counter()
    println(c)   // what the Variables view does each time it shows c
    println(c)
    println(c.next())
}
