import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// Check whether something is listening on a localhost port. (Only ever probe your own machine.)
public class Ex1 {
    static boolean isOpen(int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 200);
            System.out.println("  connected from ephemeral port " + s.getLocalPort());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) throws IOException {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (ServerSocket mine = new ServerSocket(0, 50, loopback)) {
            int closed;
            try (ServerSocket tmp = new ServerSocket(0, 50, loopback)) { closed = tmp.getLocalPort(); }
            for (int port : List.of(mine.getLocalPort(), closed)) {
                System.out.println("port " + port + ": " + (isOpen(port) ? "OPEN" : "closed"));
            }
        }
    }
}
