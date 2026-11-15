import java.util.zip.CRC32

data class Recovered(val data: Map<String, String>, val validLength: Int)

// CRC-32 of the payload's UTF-8 bytes, as exactly 8 lowercase hex digits.
fun crcHex(payload: String): String {
    TODO("CRC32().update(bytes), then format crc.value as 8 hex digits")
}

// How the writer builds each record. Keys contain no '=' and no newlines.
fun encode(key: String, value: String): String {
    val payload = "$key=$value"
    return "${crcHex(payload)} $payload\n"
}

fun recover(log: String): Recovered {
    TODO("walk line by line; stop at the first record without its newline or with a bad CRC")
}
