import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

// UDP: no connection, no stream. Each send() is one datagram; each receive() gets one.
fun main() {
    val loopback = InetAddress.getLoopbackAddress()
    DatagramSocket(InetSocketAddress(loopback, 0)).use { receiver ->
        DatagramSocket().use { sender ->                     // no connect, no handshake
            val to = InetSocketAddress(loopback, receiver.localPort)
            for (m in listOf("one", "two", "three", "truncate-me-please")) {
                val bytes = m.toByteArray()
                sender.send(DatagramPacket(bytes, bytes.size, to))
            }
            val buf = ByteArray(8)                           // deliberately small
            repeat(4) {
                val packet = DatagramPacket(buf, buf.size)
                receiver.receive(packet)                     // blocks until one datagram arrives
                println("datagram from port ${packet.port}: \"${String(packet.data, 0, packet.length)}\"")
            }
        }
    }
}
