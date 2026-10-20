fun main() {
    val powersOfTwo = generateSequence(1L) { it * 2 }         // seed, then "next from previous"
    println(powersOfTwo.take(10).toList())

    val fibonacci = sequence {                                 // a builder that yields on demand
        var a = 0L
        var b = 1L
        while (true) {                                         // infinite, but only runs when asked
            yield(a)
            val next = a + b
            a = b
            b = next
        }
    }
    println(fibonacci.take(10).toList())
    println(fibonacci.first { it > 1_000 })

    val countdown = generateSequence(5) { if (it > 1) it - 1 else null }   // null ends it
    println(countdown.toList())

    println(generateSequence { "ha" }.take(3).joinToString(""))
}
