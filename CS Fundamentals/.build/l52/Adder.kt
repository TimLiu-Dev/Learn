// The gates from Gates.kt, with a counter so we can see what an addition costs.
var nands = 0L
fun nand(a: Int, b: Int): Int { nands++; return if (a == 1 && b == 1) 0 else 1 }
fun not(a: Int) = nand(a, a)
fun and(a: Int, b: Int) = not(nand(a, b))
fun or(a: Int, b: Int) = nand(not(a), not(b))
fun xor(a: Int, b: Int): Int { val n = nand(a, b); return nand(nand(a, n), nand(b, n)) }

// Half adder: adds two bits. Returns (sum, carry).
fun halfAdder(a: Int, b: Int) = Pair(xor(a, b), and(a, b))

// Full adder: adds three bits (two inputs plus the carry coming in).
fun fullAdder(a: Int, b: Int, carryIn: Int): Pair<Int, Int> {
    val (s1, c1) = halfAdder(a, b)
    val (sum, c2) = halfAdder(s1, carryIn)
    return Pair(sum, or(c1, c2))
}

fun bit(x: Int, i: Int) = (x shr i) and 1   // read wire i of a byte (L48)

// 8-bit ripple-carry adder: eight full adders, each carry wired into the next.
// shl and or here only place the output wires; all the logic is nand.
fun add8(a: Int, b: Int, carryIn: Int = 0): Pair<Int, Int> {
    var carry = carryIn
    var sum = 0
    for (i in 0 until 8) {
        val (s, c) = fullAdder(bit(a, i), bit(b, i), carry)
        sum = sum or (s shl i)
        carry = c
    }
    return Pair(sum, carry)   // (8-bit sum, carry out of bit 7)
}

fun not8(x: Int): Int {
    var r = 0
    for (i in 0 until 8) r = r or (not(bit(x, i)) shl i)
    return r
}

// Two's complement (L2): a - b = a + (not b) + 1. The "+ 1" is the carry in.
fun sub8(a: Int, b: Int) = add8(a, not8(b), carryIn = 1).first

fun main() {
    nands = 0
    val (s, c) = add8(200, 100)
    println("200 + 100 -> sum=$s carry=$c   (300 - 256 = 44)")
    println("one 8-bit addition used $nands nand gates")
    println("100 + 100 -> ${add8(100, 100).first}, as a signed byte: ${add8(100, 100).first.toByte()}")
    println("5 - 7 -> ${sub8(5, 7)}, as a signed byte: ${sub8(5, 7).toByte()}")

    var wrongAdd = 0
    var wrongSub = 0
    for (a in 0..255) for (b in 0..255) {
        val (sum, carry) = add8(a, b)
        if (sum != (a + b) and 0xFF || carry != (a + b) shr 8) wrongAdd++
        if (sub8(a, b) != (a - b) and 0xFF) wrongSub++
    }
    println("all 65,536 pairs: wrong additions = $wrongAdd, wrong subtractions = $wrongSub")
}
