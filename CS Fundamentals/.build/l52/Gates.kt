// One primitive gate, nand. Every other gate is built from it and nothing else.
// Bits are Ints that are always 0 or 1, like wires.
fun nand(a: Int, b: Int): Int = if (a == 1 && b == 1) 0 else 1

fun not(a: Int) = nand(a, a)
fun and(a: Int, b: Int) = not(nand(a, b))
fun or(a: Int, b: Int) = nand(not(a), not(b))
fun xor(a: Int, b: Int): Int {
    val n = nand(a, b)
    return nand(nand(a, n), nand(b, n))   // the classic 4-nand xor
}

fun main() {
    println("a b | nand  not a  and  or  xor")
    var wrong = 0
    for (a in 0..1) for (b in 0..1) {
        println("$a $b |  ${nand(a, b)}      ${not(a)}     ${and(a, b)}    ${or(a, b)}   ${xor(a, b)}")
        // Check each gate against Kotlin's own bitwise operators (L48)
        if (and(a, b) != (a and b) || or(a, b) != (a or b) || xor(a, b) != (a xor b) || not(a) != (1 - a)) wrong++
    }
    println("rows that disagree with Kotlin's and/or/xor: $wrong")
}
