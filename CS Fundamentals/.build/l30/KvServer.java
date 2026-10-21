import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// A tiny Redis: SET key value / GET key / DEL key / QUIT, one line per command.
public class KvServer implements AutoCloseable {
    private final Map<String, String> store = new ConcurrentHashMap<>(); // shared by every client
    private final ServerSocket server;

    KvServer(int port) throws Exception {
        server = new ServerSocket(port, 1024, InetAddress.getLoopbackAddress());
        Thread.ofPlatform().daemon().start(() -> {
            while (true) {
                Socket s;
                try { s = server.accept(); } catch (Exception e) { break; }  // closed = stop
                Thread.ofVirtual().start(() -> session(s));                 // one per client
            }
        });
    }

    int port() { return server.getLocalPort(); }
    int size() { return store.size(); }

    // The protocol, as a pure function: easy to read, easy to test. null means "hang up".
    String respond(String line) {
        String[] parts = line.trim().split(" ", 3);          // the value may contain spaces
        return switch (parts[0].toUpperCase()) {
            case "SET" -> {
                if (parts.length != 3) yield "ERR usage: SET key value";
                store.put(parts[1], parts[2]);
                yield "OK";
            }
            case "GET" -> {
                if (parts.length != 2) yield "ERR usage: GET key";
                String v = store.get(parts[1]);
                yield v == null ? "NOT_FOUND" : "VALUE " + v;
            }
            case "DEL" -> {
                if (parts.length != 2) yield "ERR usage: DEL key";
                yield store.remove(parts[1]) != null ? "OK" : "NOT_FOUND";
            }
            case "QUIT" -> null;
            default -> "ERR unknown command";
        };
    }

    private void session(Socket socket) {
        try (socket;
             var input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new PrintWriter(socket.getOutputStream(), true)) {
            String line;
            while ((line = input.readLine()) != null) {      // null = client hung up
                String reply = respond(line);
                if (reply == null) { out.println("BYE"); break; }
                out.println(reply);
            }
        } catch (Exception e) {
            System.out.println("session: " + e);
        }
    }

    @Override public void close() throws Exception { server.close(); }

    static void selfTest() throws Exception {
        try (KvServer kv = new KvServer(0)) {
            long start = System.nanoTime();
            int wrong = 0;
            try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
                List<Future<Integer>> clients = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                    int c = i;
                    clients.add(pool.submit(() -> {
                        try (Socket s = new Socket(InetAddress.getLoopbackAddress(), kv.port());
                             var out = new PrintWriter(s.getOutputStream(), true);
                             var in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                            int bad = 0;
                            for (int k = 1; k <= 100; k++) {
                                out.println("SET c" + c + ":k" + k + " v" + c + "-" + k);
                                if (!"OK".equals(in.readLine())) bad++;
                                out.println("GET c" + c + ":k" + k);
                                if (!("VALUE v" + c + "-" + k).equals(in.readLine())) bad++;
                            }
                            out.println("QUIT");
                            if (!"BYE".equals(in.readLine())) bad++;
                            return bad;
                        }
                    }));
                }
                for (Future<Integer> f : clients) wrong += f.get();
            }
            long ms = (System.nanoTime() - start) / 1_000_000;
            System.out.println("100 clients x 100 SET/GET pairs: " + wrong + " wrong replies, "
                + kv.size() + " keys, " + ms + " ms");

            try (Socket s = new Socket(InetAddress.getLoopbackAddress(), kv.port());
                 var out = new PrintWriter(s.getOutputStream(), true);
                 var in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                for (String cmd : List.of("GET missing", "SET greeting hello world", "GET greeting",
                                          "DEL greeting", "DEL greeting", "FLY away", "QUIT")) {
                    out.println(cmd);
                    System.out.printf("  %-26s -> %s%n", cmd, in.readLine());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("serve")) {    // KvServer serve 18131
            new KvServer(Integer.parseInt(args[1]));
            System.out.println("serving on 127.0.0.1:" + args[1] + " (Ctrl-C to stop)");
            Thread.currentThread().join();                   // wait forever
        } else {
            selfTest();
        }
    }
}
