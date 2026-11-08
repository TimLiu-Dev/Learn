import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Session fixation: the attacker doesn't steal your session id. They give you one, and wait.
public class Fixation {
    static final SecureRandom rng = new SecureRandom();
    static final Map<String, String> sessions = new ConcurrentHashMap<>();   // id -> user name, or "" before login
    static volatile boolean rotateOnLogin = false;

    static String newId() {
        var b = new byte[32];
        rng.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    static String cookieId(HttpExchange ex) {
        var c = ex.getRequestHeaders().getFirst("Cookie");
        if (c == null || !c.contains("sid=")) return null;
        var v = c.substring(c.indexOf("sid=") + 4);
        return v.contains(";") ? v.substring(0, v.indexOf(';')) : v;
    }
    static void reply(HttpExchange ex, int status, String body) throws IOException {
        var bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (var out = ex.getResponseBody()) { out.write(bytes); }
    }
    static void setCookie(HttpExchange ex, String id) {
        ex.getResponseHeaders().add("Set-Cookie", "sid=" + id + "; Secure; HttpOnly; SameSite=Lax; Path=/");
    }

    public static void main(String[] args) throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/shop", ex -> {                      // anyone browsing gets an anonymous session (a cart)
            var id = newId();
            sessions.put(id, "");
            setCookie(ex, id);
            reply(ex, 200, "welcome");
        });
        server.createContext("/login", ex -> {                     // password check skipped here: see Sessions
            var old = cookieId(ex);
            if (!rotateOnLogin && old != null && sessions.containsKey(old)) {
                sessions.put(old, "ana");                          // upgrade the session the browser already had
            } else {
                if (old != null) sessions.remove(old);             // privilege changed: throw the old id away
                var id = newId();
                sessions.put(id, "ana");
                setCookie(ex, id);
            }
            reply(ex, 200, "logged in");
        });
        server.createContext("/me", ex -> {
            var id = cookieId(ex);
            var user = id == null ? null : sessions.get(id);
            if (user == null || user.isEmpty()) reply(ex, 401, "please log in"); else reply(ex, 200, "you are " + user);
        });
        server.start();
        var base = "http://127.0.0.1:" + server.getAddress().getPort();
        var client = HttpClient.newHttpClient();
        try {
            for (boolean rotate : new boolean[] {false, true}) {
                rotateOnLogin = rotate;
                System.out.println(rotate ? "-- fixed: new id on login --" : "-- vulnerable: same id before and after login --");
                var planted = idFrom(call(client, base + "/shop", null, false).headers().firstValue("Set-Cookie").orElseThrow());
                System.out.println("attacker gets an anonymous id and plants it in Ana's browser: " + planted.substring(0, 8) + "…");
                var login = call(client, base + "/login", planted, true);
                var anaId = login.headers().firstValue("Set-Cookie").map(Fixation::idFrom).orElse(planted);
                System.out.println("Ana logs in; her id is now " + anaId.substring(0, 8) + "…");
                var a = call(client, base + "/me", planted, false);
                System.out.println("attacker, using the planted id: " + a.statusCode() + " " + a.body());
                var b = call(client, base + "/me", anaId, false);
                System.out.println("Ana, using her id:              " + b.statusCode() + " " + b.body());
            }
        } finally {
            server.stop(0);
        }
    }

    static String idFrom(String setCookie) { return setCookie.substring(4, setCookie.indexOf(';')); }

    static HttpResponse<String> call(HttpClient client, String url, String id, boolean post) throws Exception {
        var req = HttpRequest.newBuilder(URI.create(url));
        if (id != null) req.header("Cookie", "sid=" + id);
        if (post) req.POST(HttpRequest.BodyPublishers.noBody());
        return client.send(req.build(), HttpResponse.BodyHandlers.ofString());
    }
}
