import java.math.BigInteger

// A DFA that reads a binary number one bit at a time and tracks its remainder mod 3.
// Reading bit b turns value v into 2v + b, so remainder r becomes (2r + b) mod 3.
enum class Rem { R0, R1, R2 }

fun step(q: Rem, bit: Char): Rem = when (q) {      // exhaustive: no else needed (L7)
    Rem.R0 -> if (bit == '0') Rem.R0 else Rem.R1
    Rem.R1 -> if (bit == '0') Rem.R2 else Rem.R0
    Rem.R2 -> if (bit == '0') Rem.R1 else Rem.R2
}

fun divisibleBy3(bits: String): Boolean {
    var q = Rem.R0                                  // the start state
    for (c in bits) {
        require(c == '0' || c == '1') { "not a bit: $c" }
        q = step(q, c)
    }
    return q == Rem.R0                              // R0 is the only accepting state
}

fun main() {
    for (s in listOf("110", "1001", "111")) {
        val trace = s.runningFold(Rem.R0) { q, c -> step(q, c) }
        println("$s (${s.toInt(2)}): ${trace.joinToString(" -> ")}  accept=${divisibleBy3(s)}")
    }
    // Check the machine against real arithmetic for every bit string of length 1..16
    var checked = 0
    var wrong = 0
    for (len in 1..16) for (v in 0 until (1 shl len)) {
        val s = Integer.toBinaryString(v).padStart(len, '0')
        checked++
        if (divisibleBy3(s) != (v % 3 == 0)) wrong++
    }
    println("checked $checked strings, $wrong disagreements")
    // One variable of memory, however long the input
    val rnd = java.util.Random(51)
    val big = buildString { repeat(1_000_000) { append(if (rnd.nextBoolean()) '1' else '0') } }
    println("1,000,000-bit number: dfa=${divisibleBy3(big)} " +
        "BigInteger=${BigInteger(big, 2).mod(BigInteger.valueOf(3)) == BigInteger.ZERO}")
}
