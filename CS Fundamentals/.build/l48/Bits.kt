fun bin(x: Int, width: Int = 8) = Integer.toBinaryString(x).padStart(width, '0').takeLast(width)

fun main() {
    val a = 0b1100   // 12
    val b = 0b1010   // 10
    println("a       = ${bin(a)}")
    println("b       = ${bin(b)}")
    println("a and b = ${bin(a and b)}  (${a and b})")
    println("a or b  = ${bin(a or b)}  (${a or b})")
    println("a xor b = ${bin(a xor b)}  (${a xor b})")
    println("a shl 2 = ${bin(a shl 2)}  (${a shl 2})   a shr 2 = ${bin(a shr 2)}  (${a shr 2})")
    println("a.inv() = ${a.inv()}   (all 32 bits flipped: -a - 1)")

    val n = -16
    println("n        = ${bin(n, 32)}")
    println("n shr 2  = ${bin(n shr 2, 32)}  (${n shr 2})")
    println("n ushr 2 = ${bin(n ushr 2, 32)}  (${n ushr 2})")
    println("1 shl 31 = ${1 shl 31}   1 shl 32 = ${1 shl 32}   1L shl 32 = ${1L shl 32}")
}
