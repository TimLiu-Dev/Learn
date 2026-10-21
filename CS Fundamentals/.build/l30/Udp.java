import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

// UDP: no connection, no stream. Each send() is one datagram; each receive() gets one.
public class Udp {
    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (DatagramSocket receiver = new DatagramSocket(new InetSocketAddress(loopback, 0));
             DatagramSocket sender = new DatagramSocket()) {        // no connect, no handshake
            var to = new InetSocketAddress(loopback, receiver.getLocalPort());
            for (String m : List.of("one", "two", "three", "truncate-me-please")) {
                byte[] bytes = m.getBytes();
                sender.send(new DatagramPacket(bytes, bytes.length, to));
            }
            byte[] buf = new byte[8];                                // deliberately small
            for (int i = 0; i < 4; i++) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                receiver.receive(packet);                // blocks until one datagram arrives
                System.out.println("datagram from port " + packet.getPort() + ": \""
                    + new String(packet.getData(), 0, packet.getLength()) + "\"");
            }
        }
    }
}
