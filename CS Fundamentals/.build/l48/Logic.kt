// p -> q ("p implies q") is false only when p is true and q is false.
fun implies(p: Boolean, q: Boolean) = !p || q

fun main() {
    val tf = listOf(true, false)
    println("p      q      p->q   !q->!p  q->p")
    for (p in tf) for (q in tf) {
        val row = listOf(p, q, implies(p, q), implies(!q, !p), implies(q, p))
        println(row.joinToString(" ") { it.toString().padEnd(6) })
    }

    // De Morgan, checked on every possible input: a proof by exhaustion
    val deMorgan = tf.all { a -> tf.all { b -> !(a && b) == (!a || !b) && !(a || b) == (!a && !b) } }
    println("De Morgan holds for all inputs: $deMorgan")

    // Quantifiers: "for all" is all, "there exists" is any
    val scores = listOf(72, 88, 95)
    println("all >= 70: ${scores.all { it >= 70 }}   any == 100: ${scores.any { it == 100 }}")
    println("not all >= 90 == any < 90: ${!scores.all { it >= 90 } == scores.any { it < 90 }}")
    println("empty.all { it > 1000 }: ${emptyList<Int>().all { it > 1000 }}")   // vacuously true

    // Sets
    val a = setOf(1, 2, 3, 4)
    val b = setOf(3, 4, 5)
    println("A ∪ B = ${a union b}   A ∩ B = ${a intersect b}   A − B = ${a subtract b}")
    println("|A ∪ B| = |A| + |B| − |A ∩ B|: ${(a union b).size} = ${a.size} + ${b.size} − ${(a intersect b).size}")
}
