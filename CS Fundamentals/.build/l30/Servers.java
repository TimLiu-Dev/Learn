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

public class Servers {
    // Each request: read one line, "work" for workMs (think: a database call), reply.
    static void handle(Socket socket, long workMs) {
        try (socket) {
            String line = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
            Thread.sleep(workMs);
            new PrintWriter(socket.getOutputStream(), true).println("done " + line);
        } catch (Exception e) {
            System.out.println("handler: " + e);
        }
    }

    static void run(String title, boolean threadPerClient, int clients, long workMs) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (ServerSocket server = new ServerSocket(0, 1024, loopback)) {
            Thread.ofPlatform().daemon().start(() -> {               // the accept loop
                while (true) {
                    Socket s;
                    try { s = server.accept(); } catch (Exception e) { break; }
                    if (threadPerClient) Thread.ofVirtual().start(() -> handle(s, workMs));
                    else handle(s, workMs);                          // one client at a time
                }
            });
            long start = System.nanoTime();
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<String>> replies = new ArrayList<>();
                for (int i = 1; i <= clients; i++) {
                    int id = i;
                    replies.add(pool.submit(() -> {
                        try (Socket s = new Socket(loopback, server.getLocalPort())) {
                            new PrintWriter(s.getOutputStream(), true).println("client " + id);
                            String reply = new BufferedReader(
                                new InputStreamReader(s.getInputStream())).readLine();
                            return reply + " after " + (System.nanoTime() - start) / 1_000_000 + " ms";
                        }
                    }));
                }
                System.out.println(title);
                if (clients <= 3) {
                    for (Future<String> r : replies) System.out.println("  " + r.get());
                } else {
                    int ok = 0;
                    for (Future<String> r : replies) if (r.get().startsWith("done")) ok++;
                    System.out.println("  " + ok + " replies");
                }
            }
            System.out.println("  total: " + (System.nanoTime() - start) / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) throws Exception {
        run("A. one client at a time, 2 clients, 500 ms of work each", false, 2, 500);
        run("B. a virtual thread per client, 2 clients", true, 2, 500);
        run("C. a virtual thread per client, 1,000 clients, 100 ms each", true, 1000, 100);
    }
}
