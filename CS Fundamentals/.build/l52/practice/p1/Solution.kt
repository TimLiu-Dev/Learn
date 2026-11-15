// The only primitive. Every call is counted.
var nands = 0
fun nand(a: Int, b: Int): Int { nands++; return if (a == 1 && b == 1) 0 else 1 }

fun not(a: Int) = nand(a, a)
fun and(a: Int, b: Int) = not(nand(a, b))
fun or(a: Int, b: Int) = nand(not(a), not(b))
fun xor(a: Int, b: Int): Int { val n = nand(a, b); return nand(nand(a, n), nand(b, n)) }

// Adds three bits. Returns (sum, carryOut).
fun fullAdder(a: Int, b: Int, carryIn: Int): Pair<Int, Int> {
    val s1 = xor(a, b)
    val c1 = and(a, b)
    return Pair(xor(s1, carryIn), or(c1, and(s1, carryIn)))
}

// Bits are least significant first. Returns a.size + 1 bits: the last is the final carry.
fun addBits(a: List<Int>, b: List<Int>): List<Int> {
    require(a.size == b.size) { "both numbers need the same width" }
    var carry = 0
    val out = mutableListOf<Int>()
    for (i in a.indices) {
        val (s, c) = fullAdder(a[i], b[i], carry)
        out += s
        carry = c                     // this column's carry-out is the next column's carry-in
    }
    out += carry                      // keep the carry: it is bit n of the answer
    return out
}

// Helpers for trying things out: toBits(6, 3) == [0, 1, 1], fromBits([0, 1, 1]) == 6
fun toBits(x: Int, width: Int) = List(width) { (x shr it) and 1 }
fun fromBits(bits: List<Int>) = bits.foldIndexed(0) { i, acc, bit -> acc or (bit shl i) }
