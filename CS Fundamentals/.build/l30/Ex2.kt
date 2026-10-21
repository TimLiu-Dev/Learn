import java.net.InetAddress
import java.net.UnknownHostException

// Resolve each name twice and time it. The second pass shows the JVM's caches,
// including the 10-second negative cache for names that don't exist.
fun main() {
    val names = listOf("example.com", "example.org", "localhost", "nope.invalid")
    for (pass in 1..2) {
        println("pass $pass")
        for (name in names) {
            val start = System.nanoTime()
            val result = try {
                val all = InetAddress.getAllByName(name)
                val v6 = all.count { it is java.net.Inet6Address }
                "${all.size - v6} IPv4 + $v6 IPv6"
            } catch (e: UnknownHostException) {
                "unknown host"
            }
            println("  %-12s %-16s %6.2f ms".format(name, result, (System.nanoTime() - start) / 1e6))
        }
    }
}
