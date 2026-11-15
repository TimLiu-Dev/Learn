import java.util.zip.CRC32

data class Recovered(val data: Map<String, String>, val validLength: Int)

fun crcHex(payload: String): String {
    val crc = CRC32().apply { update(payload.toByteArray()) }
    return crc.value.toString(16)                   // no padding
}

fun encode(key: String, value: String): String {
    val payload = "$key=$value"
    return "${crcHex(payload)} $payload\n"
}

fun recover(log: String): Recovered {
    val data = HashMap<String, String>()
    var pos = 0
    while (true) {
        val end = log.indexOf('\n', pos)
        if (end < 0) break
        val line = log.substring(pos, end)
        val payload = line.drop(9)
        val valid = line.length > 9 && line[8] == ' ' && '=' in payload && crcHex(payload) == line.take(8)
        if (!valid) break
        data[payload.substringBefore('=')] = payload.substringAfter('=')
        pos = end + 1
    }
    return Recovered(data, pos)
}
