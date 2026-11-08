import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// A "bank" at http://127.0.0.1:<port> and an attacker's page at http://localhost:<port>.
// Different hosts, so to the browser they are different sites. A real headless Chrome is the victim.
public class Csrf {
    static final String CHROME = Objects.requireNonNullElse(System.getenv("CHROME"),
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
    static final SecureRandom rng = new SecureRandom();
    static final Map<String, String> csrfOf = new ConcurrentHashMap<>();   // session id -> CSRF token
    static final List<String> log = Collections.synchronizedList(new ArrayList<>());
    static String cookieAttrs = "", defence = "";
    static int bankPort, evilPort;
    static long evilDelayMs = 0;            // "aged" mode: the attacker strikes after the cookie is 2+ minutes old

    static String token() {
        var b = new byte[32];
        rng.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    static void reply(HttpExchange ex, int status, String html) throws java.io.IOException {
        var bytes = ("<p id=r>" + html + "</p>").getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (var out = ex.getResponseBody()) { out.write(bytes); }
    }

    static Map<String, String> form(HttpExchange ex) throws java.io.IOException {
        var body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        var m = new HashMap<String, String>();
        for (var kv : body.split("&")) {
            int i = kv.indexOf('=');
            if (i > 0) m.put(kv.substring(0, i), URLDecoder.decode(kv.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    static String sessionId(HttpExchange ex) {
        var header = ex.getRequestHeaders().getFirst("Cookie");
        if (header == null) return null;
        for (var c : header.split(";\\s*"))
            if (c.startsWith("sid=")) return c.substring(4);
        return null;
    }

    public static void main(String[] args) throws Exception {
        var bank = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var evil = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        bankPort = bank.getAddress().getPort();
        evilPort = evil.getAddress().getPort();
        String bankOrigin = "http://127.0.0.1:" + bankPort;

        // Ana logs in at the bank, then (in the same browser) visits the attacker's page.
        bank.createContext("/login", ex -> {
            String sid = token();
            csrfOf.put(sid, token());
            ex.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; HttpOnly; Path=/" + cookieAttrs);
            ex.getResponseHeaders().set("Location", "http://localhost:" + evilPort + "/cute-cats");
            ex.sendResponseHeaders(302, -1);
            ex.close();
        });
        bank.createContext("/transfer", ex -> {
            var sid = sessionId(ex);
            var origin = ex.getRequestHeaders().getFirst("Origin");
            var fetchSite = ex.getRequestHeaders().getFirst("Sec-Fetch-Site");
            var f = form(ex);
            String result;
            int status;
            if (sid == null || !csrfOf.containsKey(sid)) { status = 401; result = "not logged in"; }
            else if (defence.equals("token") && !csrfOf.get(sid).equals(f.get("csrf"))) { status = 403; result = "bad CSRF token"; }
            else if (defence.equals("origin") && !bankOrigin.equals(origin)) { status = 403; result = "foreign Origin"; }
            else { status = 200; result = "SENT " + f.get("amount") + " to " + f.get("to"); }
            log.add(String.format("cookie=%-3s Origin=%s Sec-Fetch-Site=%s", sid == null ? "no" : "yes", origin, fetchSite));
            reply(ex, status, status + " " + result);
        });
        // The attacker's page: an invisible form that submits itself to the bank.
        evil.createContext("/cute-cats", ex -> {
            try { Thread.sleep(evilDelayMs); } catch (InterruptedException e) { throw new RuntimeException(e); }
            var html = "<form method=POST action='" + bankOrigin + "/transfer'>"
                    + "<input name=to value=mallory><input name=amount value=100></form>"
                    + "<script>document.forms[0].submit()</script>";
            var bytes = html.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/html");
            ex.sendResponseHeaders(200, bytes.length);
            try (var out = ex.getResponseBody()) { out.write(bytes); }
        });
        bank.start();
        evil.start();
        try {
            if (args.length > 0 && args[0].equals("aged")) evilDelayMs = 125_000;
            String[][] cases = evilDelayMs > 0 ? new String[][] {{"(none), cookie 125 s old", "", ""}} : new String[][] {
                {"(no SameSite attribute)", "", ""},
                {"SameSite=Lax", "; SameSite=Lax", ""},
                {"SameSite=Strict", "; SameSite=Strict", ""},
                {"(none) + CSRF token check", "", "token"},
                {"(none) + Origin check", "", "origin"},
            };
            for (var c : cases) {
                cookieAttrs = c[1];
                defence = c[2];
                log.clear();
                String page = victimBrowser(bankOrigin + "/login");
                String shown = page.replaceAll("(?s).*<p id=\"?r\"?>(.*?)</p>.*", "$1");
                System.out.printf("%-28s -> %s%n", c[0], shown);
                for (var line : log) System.out.println("    bank saw: " + line);
            }
        } finally {
            bank.stop(0);
            evil.stop(0);
        }
    }

    // A fresh Chrome profile each time: no cookies left over from the last case.
    static String victimBrowser(String url) throws Exception {
        var profile = Files.createTempDirectory("victim");
        var p = new ProcessBuilder(CHROME, "--headless=new", "--disable-gpu", "--no-first-run",
                "--user-data-dir=" + profile, "--virtual-time-budget=5000", "--dump-dom", url)
                .redirectError(ProcessBuilder.Redirect.DISCARD).start();
        var out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        p.waitFor();
        return out;
    }
}
