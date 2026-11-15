// The only primitive. Every call is counted.
var nands = 0
fun nand(a: Int, b: Int): Int { nands++; return if (a == 1 && b == 1) 0 else 1 }

fun not(a: Int) = nand(a, a)
fun and(a: Int, b: Int) = not(nand(a, b))
fun or(a: Int, b: Int) = nand(not(a), not(b))
fun xor(a: Int, b: Int): Int { val n = nand(a, b); return nand(nand(a, n), nand(b, n)) }

// Adds three bits. Returns (sum, carryOut).
fun fullAdder(a: Int, b: Int, carryIn: Int): Pair<Int, Int> = TODO("two half adders (xor, and) and an or")

// Bits are least significant first. Returns a.size + 1 bits: the last is the final carry.
fun addBits(a: List<Int>, b: List<Int>): List<Int> = TODO("ripple the carry from index 0 upwards")

// Helpers for trying things out: toBits(6, 3) == [0, 1, 1], fromBits([0, 1, 1]) == 6
fun toBits(x: Int, width: Int) = List(width) { (x shr it) and 1 }
fun fromBits(bits: List<Int>) = bits.foldIndexed(0) { i, acc, bit -> acc or (bit shl i) }
