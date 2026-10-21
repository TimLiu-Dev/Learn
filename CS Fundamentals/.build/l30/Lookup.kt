import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.Security

fun timedLookup(name: String): List<InetAddress> {
    val start = System.nanoTime()
    val all = InetAddress.getAllByName(name).toList()     // asks the OS resolver (and caches)
    val ms = (System.nanoTime() - start) / 1_000_000.0
    println("%-12s %5.1f ms  %s".format(name, ms, all.joinToString { it.hostAddress }))
    return all
}

fun main() {
    val first = timedLookup("example.com")
    timedLookup("example.com")                             // second time: JVM cache, no DNS query
    println("IPv4: ${first.count { it !is Inet6Address }}, IPv6: ${first.count { it is Inet6Address }}")

    timedLookup("localhost")                               // answered from /etc/hosts
    println("loopback: ${InetAddress.getLoopbackAddress()}")

    try {
        InetAddress.getByName("no-such-host.invalid")      // .invalid never exists (RFC 6761)
    } catch (e: UnknownHostException) {                    // Kotlin: no try required
        println("caught ${e.javaClass.simpleName}: ${e.message}")
    }
    println("networkaddress.cache.ttl = ${Security.getProperty("networkaddress.cache.ttl")}")
    println("networkaddress.cache.negative.ttl = ${Security.getProperty("networkaddress.cache.negative.ttl")}")
}
