// "192.168.1.151" -> one 32-bit Int, with 192 in the top 8 bits
fun parseIpv4(text: String): Int = TODO("split on dots, check for 4 numbers in 0..255, shift each one in")

// 24 -> 0xffffff00: prefix ones, then zeros
fun maskOf(prefix: Int): Int = TODO("ones on the left, zeros on the right; watch /0 and /32")

// Is ip inside the block, e.g. cidrContains("192.168.1.0/24", "192.168.1.77")?
fun cidrContains(cidr: String, ip: String): Boolean = TODO("compare only the bits the mask keeps")
