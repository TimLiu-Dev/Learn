class CounterTest {
    var count = 0

    fun first(): String { count++; return "first=$count" }
    fun second(): String { count++; return "second=$count" }
}

fun main() {
    // JUnit's default: a new instance for every test method
    val fresh = CounterTest().first() + " " + CounterTest().second()

    // A hand-made runner that reuses one instance
    val shared = CounterTest()
    val reused = shared.first() + " " + shared.second()

    println("$fresh | $reused")
}
