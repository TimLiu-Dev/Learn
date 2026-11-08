fun nand(a: Int, b: Int): Int = if (a == 1 && b == 1) 0 else 1
fun not(a: Int) = nand(a, a)
fun and(a: Int, b: Int) = not(nand(a, b))
fun or(a: Int, b: Int) = nand(not(a), not(b))

// The hardware "if": out = a when sel is 0, b when sel is 1.
fun mux(a: Int, b: Int, sel: Int) = or(and(a, not(sel)), and(b, sel))

// 1 when all eight wires are 0. A CPU needs this to decide a "jump if zero".
fun isZero8(x: Int): Int {
    var any = 0
    for (i in 0 until 8) any = or(any, (x shr i) and 1)
    return not(any)
}

fun main() {
    var muxWrong = 0
    for (a in 0..1) for (b in 0..1) for (sel in 0..1) {
        if (mux(a, b, sel) != (if (sel == 1) b else a)) muxWrong++
    }
    println("mux: 8 rows, wrong = $muxWrong")
    val zeros = (0..255).filter { isZero8(it) == 1 }
    println("isZero8 is 1 for: $zeros")
}
