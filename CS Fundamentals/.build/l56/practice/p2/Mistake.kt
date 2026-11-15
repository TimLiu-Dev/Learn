import java.util.zip.CRC32

data class Recovered(val data: Map<String, String>, val validLength: Int)

fun crcHex(payload: String): String {
    val crc = CRC32().apply { update(payload.toByteArray()) }
    return "%08x".format(crc.value)
}

fun encode(key: String, value: String): String {
    val payload = "$key=$value"
    return "${crcHex(payload)} $payload\n"
}

// Splits into lines, so a last line without its newline looks just as whole as the others.
fun recover(log: String): Recovered {
    val data = HashMap<String, String>()
    var pos = 0
    for (line in log.split("\n").filter { it.isNotEmpty() }) {
        val payload = line.drop(9)
        val valid = line.length > 9 && line[8] == ' ' && '=' in payload && crcHex(payload) == line.take(8)
        if (!valid) break
        data[payload.substringBefore('=')] = payload.substringAfter('=')
        pos = minOf(pos + line.length + 1, log.length)
    }
    return Recovered(data, pos)
}
