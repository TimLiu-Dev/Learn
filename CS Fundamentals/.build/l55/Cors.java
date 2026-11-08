import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

// An API at http://127.0.0.1:<port>; a page from http://localhost:<port> calls it with fetch().
// The browser decides what the page may read. The server log shows what actually arrived.
public class Cors {
    static final String CHROME = Objects.requireNonNullElse(System.getenv("CHROME"),
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
    static final List<String> log = Collections.synchronizedList(new ArrayList<>());

    static void send(HttpExchange ex, int status, String type, String body) throws java.io.IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        if (type != null) ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) try (var out = ex.getResponseBody()) { out.write(bytes); }
        ex.close();
    }

    public static void main(String[] args) throws Exception {
        var api = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        var site = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        String apiBase = "http://127.0.0.1:" + api.getAddress().getPort();
        String siteOrigin = "http://localhost:" + site.getAddress().getPort();

        api.createContext("/", ex -> {
            String path = ex.getRequestURI().getPath(), method = ex.getRequestMethod();
            log.add(String.format("%-7s %-15s Origin: %s", method, path, ex.getRequestHeaders().getFirst("Origin")));
            var h = ex.getResponseHeaders();
            boolean allowed = path.equals("/shared") || path.equals("/items-ok");   // the routes that opt in
            if (allowed) h.set("Access-Control-Allow-Origin", siteOrigin);           // an exact origin, never "*" with cookies
            if (method.equals("OPTIONS")) {                                          // a preflight
                if (allowed) {
                    h.set("Access-Control-Allow-Methods", "PUT");
                    h.set("Access-Control-Allow-Headers", "Content-Type");
                }
                send(ex, 204, null, "");
                return;
            }
            if (path.equals("/delete-account")) { send(ex, 200, "text/plain", "account deleted"); return; }
            send(ex, 200, "application/json", "{\"balance\":1234}");
        });
        // The other origin's page: five fetch() calls, results written into the page.
        site.createContext("/", ex -> send(ex, 200, "text/html", """
            <pre id=r></pre><script>
            const B = '%s', out = [];
            async function t(name, url, opts) {
              try { const r = await fetch(url, opts); out.push(name + ' -> read ' + r.status + ' ' + await r.text()); }
              catch (e) { out.push(name + ' -> blocked (' + e.name + ')'); }
            }
            (async () => {
              await t('1 GET  /private          ', B + '/private');
              await t('2 GET  /shared           ', B + '/shared');
              await t('3 PUT  /items     (JSON) ', B + '/items', {method: 'PUT', headers: {'Content-Type': 'application/json'}, body: '{}'});
              await t('4 PUT  /items-ok  (JSON) ', B + '/items-ok', {method: 'PUT', headers: {'Content-Type': 'application/json'}, body: '{}'});
              await t('5 POST /delete-account   ', B + '/delete-account', {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: 'yes'});
              document.getElementById('r').textContent = out.join('\\n');
            })();
            </script>""".formatted(apiBase)));
        api.start();
        site.start();
        try {
            var profile = Files.createTempDirectory("cors");
            var p = new ProcessBuilder(CHROME, "--headless=new", "--disable-gpu", "--no-first-run",
                    "--user-data-dir=" + profile, "--virtual-time-budget=5000", "--dump-dom", siteOrigin + "/")
                    .redirectError(ProcessBuilder.Redirect.DISCARD).start();
            var dom = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            p.waitFor();
            System.out.println("What the page's script got:");
            for (var line : dom.replaceAll("(?s).*<pre id=\"r\">(.*?)</pre>.*", "$1").split("\n"))
                System.out.println("  " + line.replace("&gt;", ">"));   // --dump-dom prints HTML, so ">" arrives escaped
            System.out.println("What the API server received:");
            for (var line : log) System.out.println("  " + line);
        } finally {
            api.stop(0);
            site.stop(0);
        }
    }
}
