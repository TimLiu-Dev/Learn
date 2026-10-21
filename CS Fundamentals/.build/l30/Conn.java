import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// Show a real TCP connection from both ends, then ask the OS (netstat) what it sees.
public class Conn {
    static void netstat(int port, String label) throws Exception {
        Process p = new ProcessBuilder("netstat", "-an", "-p", "tcp").start();
        List<String> out = p.inputReader().lines()
            .filter(line -> line.contains("." + port + " "))      // lines that mention our port
            .toList();
        System.out.println("netstat " + label + ":");
        for (String line : out) System.out.println("  " + line.trim().replaceAll("\\s+", " "));
    }

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(0, 50, InetAddress.getLoopbackAddress()); // 0 = OS picks
        int port = server.getLocalPort();
        System.out.println("server listening on " + server.getLocalSocketAddress());

        Socket client = new Socket(InetAddress.getLoopbackAddress(), port); // connect = handshake
        Socket accepted = server.accept();                                  // the server's end
        System.out.println("client end: local " + client.getLocalSocketAddress()
            + " -> remote " + client.getRemoteSocketAddress());
        System.out.println("server end: local " + accepted.getLocalSocketAddress()
            + " -> remote " + accepted.getRemoteSocketAddress());
        netstat(port, "while connected");

        client.close();           // the client closes first...
        accepted.close();
        server.close();
        Thread.sleep(200);
        netstat(port, "after close");   // ...so the client's side lingers in TIME_WAIT
    }
}
