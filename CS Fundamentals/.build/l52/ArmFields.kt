// Decode real ARM64 add/sub instructions (from objdump of clang's output) with masks and shifts.
fun field(word: Long, lo: Int, width: Int) = ((word ushr lo) and ((1L shl width) - 1)).toInt()

fun decode(word: Long): String {
    val rd = field(word, 0, 5)
    val rn = field(word, 5, 5)
    val rm = field(word, 16, 5)
    val op = if (field(word, 30, 1) == 1) "sub" else "add"
    val r = if (field(word, 31, 1) == 1) "x" else "w"   // 64-bit or 32-bit registers
    return "$op $r$rd, $r$rn, $r$rm"
}

fun main() {
    val words = listOf(0x0b0700a3L, 0x0b000020L, 0x0b080148L, 0x4b0700a3L, 0x8b0700a3L)
    for (w in words) println("%08x  ->  %s".format(w, decode(w)))
}
