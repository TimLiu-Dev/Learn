import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

fun attempt(label: String, block: () -> Unit) {
    val start = System.nanoTime()
    try {
        block()
        println("$label: ok")
    } catch (e: Exception) {                                 // Kotlin: IOExceptions are unchecked
        val ms = (System.nanoTime() - start) / 1_000_000
        println("$label: ${e.javaClass.simpleName}: ${e.message} (after $ms ms)")
    }
}

fun main() {
    val loopback = InetAddress.getLoopbackAddress()
    val closedPort = ServerSocket(0, 50, loopback).use { it.localPort }  // open, note it, close it

    attempt("1. closed port") { Socket(loopback, closedPort).close() }

    attempt("2. connect timeout") {                          // 192.0.2.1 = TEST-NET-1, RFC 5737
        Socket().use { it.connect(InetSocketAddress("192.0.2.1", 80), 500) }
    }

    ServerSocket(0, 50, loopback).use { silent ->            // accepts, but never says anything
        attempt("3. read timeout") {
            Socket(loopback, silent.localPort).use { s ->
                s.soTimeout = 300                            // SO_TIMEOUT for read()
                s.getInputStream().read()
            }
        }
    }
}
