import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

// Show a real TCP connection from both ends, then ask the OS (netstat) what it sees.
fun netstat(port: Int, label: String) {
    val out = ProcessBuilder("netstat", "-an", "-p", "tcp").start()
        .inputStream.bufferedReader().readLines()
        .filter { ".$port " in it }                          // lines that mention our port
    println("netstat $label:")
    out.forEach { println("  " + it.trim().replace(Regex("\\s+"), " ")) }
}

fun main() {
    val server = ServerSocket(0, 50, InetAddress.getLoopbackAddress())  // port 0 = OS picks one
    val port = server.localPort
    println("server listening on ${server.localSocketAddress}")

    val client = Socket(InetAddress.getLoopbackAddress(), port)   // connect() = 3-way handshake
    val accepted = server.accept()                                // the server's end of it
    println("client end: local ${client.localSocketAddress} -> remote ${client.remoteSocketAddress}")
    println("server end: local ${accepted.localSocketAddress} -> remote ${accepted.remoteSocketAddress}")
    netstat(port, "while connected")

    client.close()            // the client closes first...
    accepted.close()
    server.close()
    Thread.sleep(200)
    netstat(port, "after close")   // ...so the client's side lingers in TIME_WAIT
}
