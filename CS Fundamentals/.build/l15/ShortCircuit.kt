fun main() {
    var visited = 0                                   // Kotlin lambdas may change a captured var
    val nums = (1..1_000).asSequence().onEach { visited++ }

    val firstBig = nums.first { it * it > 50 }
    println("first square > 50: $firstBig, visited $visited")

    visited = 0
    val anyEven = nums.any { it % 2 == 0 }
    println("any even: $anyEven, visited $visited")

    visited = 0
    val firstThree = nums.map { it * 10 }.take(3).toList()
    println("take(3): $firstThree, visited $visited")

    visited = 0
    val noTerminal = nums.map { it * 10 }.filter { it > 0 }   // intermediate ops only
    println("no terminal op, visited $visited")

    visited = 0
    val eager = (1..1_000).onEach { visited++ }.first { it * it > 50 }   // a List-style chain
    println("eager chain: $eager, visited $visited")
}
