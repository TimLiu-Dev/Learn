import kotlin.random.Random

// My first Branches run built its "random" array with a fresh generator per element.
fun main() {
    val firsts = (42 until 50).map { Random(it).nextInt(256) }
    println("Random(42..49).nextInt(256): $firsts")
    val pattern = (42 until 106).joinToString("") { if (Random(it).nextInt(256) >= 128) "1" else "0" }
    println("is it >= 128, for 64 seeds:  $pattern")
    val rng = Random(42)
    println("one Random(42), 64 draws:    " + (0 until 64).joinToString("") { if (rng.nextInt(256) >= 128) "1" else "0" })
}
