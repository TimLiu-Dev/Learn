import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// A line-framed server: one virtual thread per client, one reply line per request line.
public class Ex3 {
    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (ServerSocket server = new ServerSocket(0, 50, loopback)) {
            Thread.ofPlatform().daemon().start(() -> {
                while (true) {
                    Socket s;
                    try { s = server.accept(); } catch (Exception e) { break; }
                    Thread.ofVirtual().start(() -> {
                        try (s; var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                             var out = new PrintWriter(s.getOutputStream(), true)) {
                            String line;
                            while ((line = in.readLine()) != null) out.println(line.toUpperCase());
                        } catch (Exception e) {
                            System.out.println("handler: " + e);
                        }
                    });
                }
            });
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<String>> results = new ArrayList<>();
                for (int i = 1; i <= 3; i++) {
                    int id = i;
                    results.add(pool.submit(() -> {
                        try (Socket s = new Socket(loopback, server.getLocalPort())) {
                            // two messages in ONE write: the server still sees two lines
                            s.getOutputStream().write(("client " + id + "\nsays hi\n").getBytes());
                            var in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            return in.readLine() + " | " + in.readLine();
                        }
                    }));
                }
                for (Future<String> r : results) System.out.println(r.get());
            }
        }
    }
}
