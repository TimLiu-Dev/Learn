import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// An HTTP/1.1 server on raw sockets: one request per connection.
// java TinyServer.java          -> serves 6 requests from its own client, then stops
// java TinyServer.java serve 30 -> serves on 127.0.0.1:18131 for 30 s (for curl)
public class TinyServer {
    record Request(String method, String target, Map<String, String> headers, String body) {}

    static Request readRequest(InputStream raw) throws IOException {
        var in = new BufferedInputStream(raw);
        String[] start = readLine(in).split(" ");            // GET /hello HTTP/1.1
        var headers = new LinkedHashMap<String, String>();
        for (String line = readLine(in); !line.isEmpty(); line = readLine(in)) {
            int colon = line.indexOf(':');
            headers.put(line.substring(0, colon).trim().toLowerCase(), line.substring(colon + 1).trim());
        }
        int length = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        String body = new String(in.readNBytes(length), StandardCharsets.UTF_8);
        return new Request(start[0], start[1], headers, body);
    }

    // HTTP lines end in CRLF (\r\n)
    static String readLine(InputStream in) throws IOException {
        var buf = new ByteArrayOutputStream();
        for (int b = in.read(); b != -1 && b != '\n'; b = in.read()) {
            if (b != '\r') buf.write(b);
        }
        return buf.toString(StandardCharsets.UTF_8);
    }

    static void respond(OutputStream out, int status, String reason, String body,
                        boolean head, String... extra) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        var sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(status).append(' ').append(reason).append("\r\n");
        sb.append("Content-Type: text/plain; charset=utf-8\r\n");
        sb.append("Content-Length: ").append(bytes.length).append("\r\n");
        sb.append("Connection: close\r\n");
        for (String h : extra) sb.append(h).append("\r\n");
        sb.append("\r\n");                                    // blank line: end of headers
        out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        if (!head) out.write(bytes);                          // HEAD: same headers, no body
        out.flush();
    }

    static void handle(Socket socket) {
        try (socket) {
            Request req = readRequest(socket.getInputStream());
            String path = req.target().split("\\?")[0];
            String query = req.target().contains("?") ? req.target().split("\\?", 2)[1] : "";
            boolean head = req.method().equals("HEAD");
            boolean get = head || req.method().equals("GET");
            var out = socket.getOutputStream();
            int status;
            if (get && path.equals("/")) {
                status = 200; respond(out, 200, "OK", "home\n", head);
            } else if (get && path.equals("/hello")) {
                String name = query.startsWith("name=") ? query.substring(5) : "world";
                status = 200; respond(out, 200, "OK", "hello, " + name + "\n", head);
            } else if (get && path.equals("/old")) {
                status = 301; respond(out, 301, "Moved Permanently", "", head, "Location: /new");
            } else if (get && path.equals("/new")) {
                status = 200; respond(out, 200, "OK", "you made it to /new\n", head);
            } else if (get && path.equals("/slow")) {
                Thread.sleep(2000);
                status = 200; respond(out, 200, "OK", "finally\n", head);
            } else if (req.method().equals("POST") && path.equals("/echo")) {
                status = 200;
                respond(out, 200, "OK", "got " + req.body().length() + " bytes: " + req.body() + "\n", false);
            } else {
                status = 404; respond(out, 404, "Not Found", "no route for " + path + "\n", head);
            }
            System.out.println("  [server] " + req.method() + " " + req.target() + " -> " + status);
        } catch (IOException | InterruptedException e) {
            System.out.println("  [server] error: " + e);
        }
    }

    // The client side, by hand: write a request, read until the server closes.
    static String send(int port, String request) throws IOException {
        try (var s = new Socket("127.0.0.1", port)) {
            s.getOutputStream().write(request.getBytes(StandardCharsets.UTF_8));
            return new String(s.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) throws Exception {
        boolean serve = args.length > 0 && args[0].equals("serve");
        var server = new ServerSocket();
        server.bind(new InetSocketAddress("127.0.0.1", serve ? 18131 : 0));
        if (serve) {
            long until = System.currentTimeMillis() + 1000L * Integer.parseInt(args[1]);
            server.setSoTimeout(200);
            System.out.println("serving on http://127.0.0.1:18131 for " + args[1] + " s");
            while (System.currentTimeMillis() < until) {
                try { Socket s = server.accept(); Thread.ofVirtual().start(() -> handle(s)); }
                catch (SocketTimeoutException again) { }
            }
            server.close();
            return;
        }
        int port = server.getLocalPort();
        Thread acceptor = Thread.ofPlatform().start(() -> {
            try (server) {
                for (int i = 0; i < 6; i++) {
                    Socket s = server.accept();
                    Thread.ofVirtual().start(() -> handle(s)).join();
                }
            } catch (IOException | InterruptedException e) { throw new RuntimeException(e); }
        });

        String full = send(port, "GET /hello?name=Ada HTTP/1.1\r\nHost: localhost\r\n\r\n");
        System.out.print("--- full response ---\n" + full.replace("\r\n", "\\r\\n\n"));
        System.out.println("--- status lines ---");
        String post = "{\"x\":1}";
        String[] requests = {
            "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n",
            "HEAD / HTTP/1.1\r\nHost: localhost\r\n\r\n",
            "GET /old HTTP/1.1\r\nHost: localhost\r\n\r\n",
            "POST /echo HTTP/1.1\r\nHost: localhost\r\nContent-Length: " + post.length() + "\r\n\r\n" + post,
            "GET /nope HTTP/1.1\r\nHost: localhost\r\n\r\n",
        };
        for (String r : requests) {
            String resp = send(port, r);
            String status = resp.lines().findFirst().orElse("");
            String body = resp.substring(resp.indexOf("\r\n\r\n") + 4).strip();
            System.out.println(status + (body.isEmpty() ? "   (no body)" : "   body: " + body));
        }
        acceptor.join();
    }
}
