// Protocol Buffers' base-128 varint, by hand: 7 bits per byte, low group first, top bit = "more follows".
fun encodeVarint(value: Long): ByteArray {
    val out = mutableListOf<Byte>()
    var v = value
    while (true) {
        val low7 = (v and 0x7F).toInt()
        v = v ushr 7                                   // unsigned shift: L48's >>>
        if (v == 0L) { out += low7.toByte(); return out.toByteArray() }
        out += (low7 or 0x80).toByte()                 // set the continuation bit
    }
}

fun decodeVarint(bytes: ByteArray): Long {
    var result = 0L
    for ((i, b) in bytes.withIndex()) {
        result = result or ((b.toLong() and 0x7F) shl (7 * i))
        if (b.toInt() and 0x80 == 0) break             // no continuation bit: last byte
    }
    return result
}

fun hex(bytes: ByteArray) = bytes.joinToString(" ") { "%02x".format(it) }

fun main() {
    for (n in listOf(1L, 150L, 300L)) {
        val bytes = encodeVarint(n)
        println("%3d -> %-5s -> %d".format(n, hex(bytes), decodeVarint(bytes)))
    }
    val tag = (1 shl 3) or 0                           // field number 1, wire type 0 (VARINT)
    val message = byteArrayOf(tag.toByte()) + encodeVarint(150)
    println("field 1 = 150 : ${hex(message)}  (${message.size} bytes)")
    println("JSON {\"a\":150}: ${"{\"a\":150}".length} bytes")
}
