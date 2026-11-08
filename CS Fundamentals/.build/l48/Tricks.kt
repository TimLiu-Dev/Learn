// n and (n - 1) clears the lowest 1-bit, so it's 0 only when n had exactly one 1-bit
fun isPowerOfTwo(n: Int) = n > 0 && (n and (n - 1)) == 0

fun main() {
    for (n in listOf(1, 6, 64, 96, 1024)) {
        println("%5d  %-12s  power of 2: %-5s  ones: %d  highest: %4d  lowest: %d".format(
            n, Integer.toBinaryString(n), isPowerOfTwo(n), n.countOneBits(), n.takeHighestOneBit(), n and -n))
    }

    // For a power-of-two table size, "h and (size - 1)" is h.mod(size): the low bits ARE the remainder
    val size = 16
    val keys = (1..8).map { it shl 16 }          // 65536, 131072, ...: differ only in high bits
    println("keys:            $keys")
    println("h and 15:        ${keys.map { it.hashCode() and (size - 1) }}")
    println("spread and 15:   ${keys.map { val h = it.hashCode(); (h xor (h ushr 16)) and (size - 1) }}")
    println("and == mod?      ${(-1000..1000).all { (it and (size - 1)) == it.mod(size) }}")
}
