import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// One backend server. Runs as its own JVM process: java Backend.java b1 200 1
public class Backend {
    public static void main(String[] args) throws IOException {
        String name = args[0];
        long slowMs = args.length > 1 ? Long.parseLong(args[1]) : 0;
        int threads = args.length > 2 ? Integer.parseInt(args[2]) : 1;
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 100);
        server.createContext("/health", ex -> reply(ex, 200, "ok"));
        server.createContext("/name", ex -> reply(ex, 200, name));
        server.createContext("/slow", ex -> {
            try { Thread.sleep(slowMs); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            reply(ex, 200, name);
        });
        server.createContext("/work", ex -> { hashPassword(); reply(ex, 200, name); });
        if (threads > 1) server.setExecutor(Executors.newFixedThreadPool(threads));
        server.start();                      // no executor: ONE thread serves every request
        System.out.println("READY " + server.getAddress().getPort());
    }

    static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    // CPU-bound work: one PBKDF2 hash (L33), about 5 ms
    static byte[] hashPassword() {
        try {
            var spec = new PBEKeySpec("hunter2".toCharArray(), new byte[16], 20_000, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    record Proc(String name, Process process, URI uri) {}

    // Starts a backend as a separate JVM (java Backend.java ...) and waits for READY.
    static Proc start(String name, long slowMs) throws IOException { return start(name, slowMs, 1); }

    static Proc start(String name, long slowMs, int threads) throws IOException {
        String java = ProcessHandle.current().info().command().orElse("java");
        Process p = new ProcessBuilder(java, "Backend.java", name, Long.toString(slowMs), Integer.toString(threads))
            .redirectErrorStream(true).start();
        String ready = p.inputReader().readLine();          // "READY 51234"
        int port = Integer.parseInt(ready.substring("READY ".length()));
        return new Proc(name, p, URI.create("http://127.0.0.1:" + port));
    }
}
