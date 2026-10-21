import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Timeouts {
    interface Action { void run() throws Exception; }

    static void attempt(String label, Action block) {
        long start = System.nanoTime();
        try {
            block.run();
            System.out.println(label + ": ok");
        } catch (Exception e) {
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.println(label + ": " + e.getClass().getSimpleName() + ": " + e.getMessage()
                + " (after " + ms + " ms)");
        }
    }

    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        int closedPort;
        try (ServerSocket tmp = new ServerSocket(0, 50, loopback)) { closedPort = tmp.getLocalPort(); }

        attempt("1. closed port", () -> new Socket(loopback, closedPort).close());

        attempt("2. connect timeout", () -> {                  // 192.0.2.1 = TEST-NET-1, RFC 5737
            try (Socket s = new Socket()) { s.connect(new InetSocketAddress("192.0.2.1", 80), 500); }
        });

        try (ServerSocket silent = new ServerSocket(0, 50, loopback)) {  // accepts, never talks
            attempt("3. read timeout", () -> {
                try (Socket s = new Socket(loopback, silent.getLocalPort())) {
                    s.setSoTimeout(300);                         // SO_TIMEOUT for read()
                    s.getInputStream().read();
                }
            });
        }
    }
}
