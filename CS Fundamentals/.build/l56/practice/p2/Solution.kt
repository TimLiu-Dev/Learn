import java.util.zip.CRC32

data class Recovered(val data: Map<String, String>, val validLength: Int)

// CRC-32 of the payload's UTF-8 bytes, as exactly 8 lowercase hex digits.
fun crcHex(payload: String): String {
    val crc = CRC32().apply { update(payload.toByteArray()) }
    return "%08x".format(crc.value)                 // pad: a CRC can start with zero digits
}

// How the writer builds each record. Keys contain no '=' and no newlines.
fun encode(key: String, value: String): String {
    val payload = "$key=$value"
    return "${crcHex(payload)} $payload\n"
}

fun recover(log: String): Recovered {
    val data = HashMap<String, String>()
    var pos = 0                                     // end of the last good record
    while (true) {
        val end = log.indexOf('\n', pos)
        if (end < 0) break                          // no newline: the record was never finished
        val line = log.substring(pos, end)
        val payload = line.drop(9)
        val valid = line.length > 9 && line[8] == ' ' && '=' in payload && crcHex(payload) == line.take(8)
        if (!valid) break                           // nothing after a bad record can be trusted
        data[payload.substringBefore('=')] = payload.substringAfter('=')
        pos = end + 1
    }
    return Recovered(data, pos)
}
