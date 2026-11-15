fun parseIpv4(text: String): Int {
    val parts = text.split(".")
    require(parts.size == 4) { "an IPv4 address has 4 numbers: $text" }
    var ip = 0
    for (part in parts) {
        val n = part.toInt()                    // NumberFormatException is an IllegalArgumentException
        require(n in 0..255) { "$part is not in 0..255" }
        ip = (ip shl 8) or n                    // make room, then drop the next 8 bits in
    }
    return ip                                   // 192.x.x.x comes out negative: the top bit is set (L2)
}

fun maskOf(prefix: Int): Int {
    require(prefix in 0..32) { "prefix must be 0..32: $prefix" }
    // An Int shift uses only the low 5 bits of the distance, so -1 shl 32 is still -1 (L48).
    return if (prefix == 0) 0 else -1 shl (32 - prefix)
}

fun cidrContains(cidr: String, ip: String): Boolean {
    val (network, prefix) = cidr.split("/")
    val mask = maskOf(prefix.toInt())
    return (parseIpv4(ip) and mask) == (parseIpv4(network) and mask)
}
