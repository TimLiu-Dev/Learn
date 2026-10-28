import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Sessions {
    // Version 1: each server remembers sessions in its own memory.
    static HttpServer stateful(String name) throws IOException {
        Map<String, String> sessions = new ConcurrentHashMap<>();       // session id -> user
        HttpServer s = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50);
        s.createContext("/login", ex -> {
            String id = UUID.randomUUID().toString().substring(0, 8);
            sessions.put(id, ex.getRequestURI().getQuery().replaceFirst("^user=", ""));
            send(ex, 200, id);
        });
        s.createContext("/me", ex -> {
            String sid = ex.getRequestHeaders().getFirst("Session");
            String user = sid == null ? null : sessions.get(sid);
            if (user == null) send(ex, 401, "who are you? (" + name + " has no such session)");
            else send(ex, 200, "hello " + user + " (from " + name + ")");
        });
        s.start();
        return s;
    }

    // Version 2: the server remembers nothing; the client carries a signed token (L33's HMAC).
    static HttpServer stateless(String name, byte[] key) throws IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 50);
        s.createContext("/login", ex -> {
            String user = ex.getRequestURI().getQuery().replaceFirst("^user=", "");
            send(ex, 200, user + "." + sign(user, key));
        });
        s.createContext("/me", ex -> {
            String token = ex.getRequestHeaders().getFirst("Token");
            String[] parts = (token == null ? "." : token).split("\\.", 2);
            boolean valid = parts.length == 2                                              // constant time (L33)
                && MessageDigest.isEqual(parts[1].getBytes(), sign(parts[0], key).getBytes());
            if (valid) send(ex, 200, "hello " + parts[0] + " (from " + name + ")");
            else send(ex, 401, "bad signature");
        });
        s.start();
        return s;
    }

    static String sign(String user, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(user.getBytes())).substring(0, 16);
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    static String call(HttpServer server, String path, String header, String value) throws Exception {
        var b = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path));
        if (header != null) b.header(header, value);
        var resp = client.send(b.build(), HttpResponse.BodyHandlers.ofString());
        return resp.statusCode() + " " + resp.body();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("-- stateful: sessions live in each server's memory");
        HttpServer a = stateful("A"), b = stateful("B");
        String session = call(a, "/login?user=ada", null, null).split(" ", 2)[1];
        System.out.println("login    -> A: session " + session);
        System.out.println("GET /me  -> B: " + call(b, "/me", "Session", session));   // the balancer picked B
        System.out.println("GET /me  -> A: " + call(a, "/me", "Session", session));

        System.out.println("-- stateless: a signed token, nothing remembered");
        byte[] key = "shared-secret-from-config".getBytes();                          // same key on every server
        HttpServer c = stateless("A", key), d = stateless("B", key);
        String token = call(c, "/login?user=ada", null, null).split(" ", 2)[1];
        System.out.println("login    -> A: token " + token);
        System.out.println("GET /me  -> B: " + call(d, "/me", "Token", token));
        System.out.println("GET /me  -> A: " + call(c, "/me", "Token", token));
        System.out.println("forged   -> B: " + call(d, "/me", "Token", "admin." + token.split("\\.", 2)[1]));
        for (HttpServer s : new HttpServer[] { a, b, c, d }) s.stop(0);
    }
}
