fun main() {
    val word = "kotlin"
    val a = word.let { it.uppercase() }
    val b = word.also { it.uppercase() }
    val c = word.apply { uppercase() }
    val d = word.run { uppercase() }
    println("$a $b $c $d")
}
