fun main() {
    // 192.168.1.77, one octet per 8 bits
    val ip = (192 shl 24) or (168 shl 16) or (1 shl 8) or 77
    println(ip)
}
