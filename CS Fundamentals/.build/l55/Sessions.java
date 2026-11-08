import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Sessions {
    // ---- passwords: L33's PBKDF2, compressed ----
    static final SecureRandom rng = new SecureRandom();
    record Hash(byte[] salt, byte[] hash) {
        boolean matches(String pw) { return MessageDigest.isEqual(pbkdf2(pw, salt), hash); }
    }
    static byte[] pbkdf2(String pw, byte[] salt) {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(new PBEKeySpec(pw.toCharArray(), salt, 600_000, 256)).getEncoded();
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }
    static Hash hashOf(String pw) {
        var salt = new byte[16];
        rng.nextBytes(salt);
        return new Hash(salt, pbkdf2(pw, salt));
    }
    static final Map<String, Hash> users = Map.of("ana", hashOf("correct horse"));
    static final Hash dummy = hashOf("no such user");   // checked when the name is unknown, so both failures cost the same

    // ---- sessions: the browser holds a random id, the server holds what it means ----
    static final Map<String, String> sessions = new ConcurrentHashMap<>();   // session id -> user name
    static final String COOKIE = "__Host-sid";
    static String newSessionId() {
        var b = new byte[32];                                                  // 256 bits from the OS's CSPRNG
        rng.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    static String sessionId(HttpExchange ex) {
        var header = ex.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (var c : header.split(";"))
            if (c.trim().startsWith(COOKIE + "=")) return c.trim().substring(COOKIE.length() + 1);
        return null;
    }
    static String sessionUser(HttpExchange ex) {
        var sid = sessionId(ex);
        return sid == null ? null : sessions.get(sid);
    }

    static void reply(HttpExchange ex, int status, String body) throws IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) try (var out = ex.getResponseBody()) { out.write(bytes); }
        ex.close();
    }
    static Map<String, String> form(HttpExchange ex) throws IOException {
        var m = new HashMap<String, String>();
        for (var kv : new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).split("&")) {
            int i = kv.indexOf('=');
            if (i > 0) m.put(kv.substring(0, i), URLDecoder.decode(kv.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    static void login(HttpExchange ex, boolean quickExit) throws IOException {
        var f = form(ex);
        Hash stored = f.get("name") == null ? null : users.get(f.get("name"));
        if (quickExit && stored == null) { reply(ex, 401, "invalid name or password"); return; }  // OWASP's "quick exit" leak
        boolean ok = (stored != null ? stored : dummy).matches(f.getOrDefault("password", "")) && stored != null;
        if (!ok) { reply(ex, 401, "invalid name or password"); return; }   // same words for both failures
        var sid = newSessionId();
        sessions.put(sid, f.get("name"));
        ex.getResponseHeaders().add("Set-Cookie", COOKIE + "=" + sid + "; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=1800");
        reply(ex, 204, "");
    }

    static void show(String label, String result) { System.out.println(String.format("%-28s", label) + "-> " + result); }

    public static void main(String[] args) throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/login", ex -> login(ex, false));
        server.createContext("/login-quick", ex -> login(ex, true));
        server.createContext("/me", ex -> {
            var user = sessionUser(ex);
            if (user != null) reply(ex, 200, "you are " + user); else reply(ex, 401, "please log in");
        });
        server.createContext("/logout", ex -> {
            var sid = sessionId(ex);
            if (sid != null) sessions.remove(sid);
            ex.getResponseHeaders().add("Set-Cookie", COOKIE + "=; Secure; HttpOnly; SameSite=Lax; Path=/; Max-Age=0");
            reply(ex, 204, "");
        });
        server.start();
        var base = "http://127.0.0.1:" + server.getAddress().getPort();
        var client = HttpClient.newHttpClient();                       // no cookie jar: we handle the header, like curl
        try {
            show("wrong password", timed(client, base + "/login", "ana", "wrong"));
            show("unknown user", timed(client, base + "/login", "zed", "wrong"));
            show("unknown user, quick exit", timed(client, base + "/login-quick", "zed", "wrong"));
            var ok = post(client, base + "/login", "ana", "correct horse");
            var setCookie = ok.headers().firstValue("Set-Cookie").orElseThrow();
            show("right password", String.valueOf(ok.statusCode()));
            System.out.println("  Set-Cookie: " + setCookie.substring(0, 24) + "…" + setCookie.substring(setCookie.indexOf(';') + 1));
            var cookie = setCookie.substring(0, setCookie.indexOf(';'));   // what a browser sends back
            show("GET /me with the cookie", me(client, base, cookie));
            show("GET /me without it", me(client, base, null));
            show("GET /me with a guessed id", me(client, base, COOKIE + "=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            var out = client.send(HttpRequest.newBuilder(URI.create(base + "/logout")).header("Cookie", cookie)
                    .POST(HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
            var cleared = out.headers().firstValue("Set-Cookie").orElseThrow();
            show("POST /logout", out.statusCode() + ", Set-Cookie: …" + cleared.substring(cleared.lastIndexOf(';') + 1));
            show("GET /me with the old cookie", me(client, base, cookie));
            System.out.println("sessions left on the server: " + sessions.size());
        } finally {
            server.stop(0);
        }
    }

    static HttpResponse<String> post(HttpClient client, String url, String name, String pw) throws Exception {
        var body = "name=" + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&password=" + URLEncoder.encode(pw, StandardCharsets.UTF_8);
        var req = HttpRequest.newBuilder(URI.create(url)).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
    static String me(HttpClient client, String base, String cookie) throws Exception {
        var req = HttpRequest.newBuilder(URI.create(base + "/me"));
        if (cookie != null) req.header("Cookie", cookie);
        var r = client.send(req.build(), HttpResponse.BodyHandlers.ofString());
        return r.statusCode() + " " + r.body();
    }
    static String timed(HttpClient client, String url, String name, String pw) throws Exception {
        for (int i = 0; i < 3; i++) post(client, url, name, pw);       // warm up (L16)
        long t0 = System.nanoTime();
        var r = post(client, url, name, pw);
        return r.statusCode() + " " + r.body() + "  (" + (System.nanoTime() - t0) / 1_000_000 + " ms)";
    }
}
