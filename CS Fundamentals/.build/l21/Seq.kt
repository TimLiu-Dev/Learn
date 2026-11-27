fun main() {
    val steps = mutableListOf("compile", "test", "jar")
    val backwards = steps.asReversed()       // a view, like Java's reversed()
    steps.add("publish")
    println("${steps.first()} .. ${steps.last()}  reversed $backwards")

    val seen = linkedMapOf("ada" to 3, "ben" to 1, "cy" to 2)
    seen.putFirst("cy", 5)                   // a JDK 21 member, called directly
    println("${seen.firstEntry()} .. ${seen.lastEntry()}  ${seen.sequencedKeySet().reversed()}")
}
