import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

// Check whether something is listening on a localhost port. (Only ever probe your own machine.)
fun isOpen(port: Int): Boolean = try {
    Socket().use {
        it.connect(InetSocketAddress(InetAddress.getLoopbackAddress(), port), 200)
        println("  connected from ephemeral port ${it.localPort}")
        true
    }
} catch (e: java.io.IOException) {
    false
}

fun main() {
    val loopback = InetAddress.getLoopbackAddress()
    ServerSocket(0, 50, loopback).use { mine ->
        val closed = ServerSocket(0, 50, loopback).use { it.localPort }
        for (port in listOf(mine.localPort, closed)) {
            println("port $port: ${if (isOpen(port)) "OPEN" else "closed"}")
        }
    }
}
