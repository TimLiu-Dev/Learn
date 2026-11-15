fun parseIpv4(text: String): Int {
    val parts = text.split(".")
    require(parts.size == 4) { "an IPv4 address has 4 numbers: $text" }
    var ip = 0
    for (part in parts) {
        val n = part.toInt()
        require(n in 0..255) { "$part is not in 0..255" }
        ip = (ip shl 8) or n
    }
    return ip
}

fun maskOf(prefix: Int): Int = -1 shl (32 - prefix)

fun cidrContains(cidr: String, ip: String): Boolean {
    val (network, prefix) = cidr.split("/")
    val mask = maskOf(prefix.toInt())
    return (parseIpv4(ip) and mask) == (parseIpv4(network) and mask)
}
