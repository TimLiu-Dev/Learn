fun main() {
    val evens = (1..6).asSequence().filter { it % 2 == 0 }
    println(evens.toList())
    println(evens.count())          // a second terminal op: fine, the pipeline just runs again

    val once = evens.constrainOnce()
    println(once.toList())
    println(once.count())           // second iteration of a constrained sequence
}
