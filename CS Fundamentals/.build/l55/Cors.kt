import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.Collections

// An API at http://127.0.0.1:<port>; a page from http://localhost:<port> calls it with fetch().
// The browser decides what the page may read. The server log shows what actually arrived.
val chrome = System.getenv("CHROME") ?: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
val log: MutableList<String> = Collections.synchronizedList(mutableListOf())

fun send(ex: HttpExchange, status: Int, type: String?, body: String) {
    val bytes = body.toByteArray()
    type?.let { ex.responseHeaders.set("Content-Type", it) }
    ex.sendResponseHeaders(status, if (bytes.isEmpty()) -1 else bytes.size.toLong())
    if (bytes.isNotEmpty()) ex.responseBody.use { it.write(bytes) }
    ex.close()
}

fun main() {
    val api = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    val site = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    val apiBase = "http://127.0.0.1:${api.address.port}"
    val siteOrigin = "http://localhost:${site.address.port}"

    api.createContext("/") { ex ->
        val path = ex.requestURI.path
        val method = ex.requestMethod
        log += "%-7s %-15s Origin: %s".format(method, path, ex.requestHeaders.getFirst("Origin"))
        val allowed = path == "/shared" || path == "/items-ok"                     // the routes that opt in
        if (allowed) ex.responseHeaders.set("Access-Control-Allow-Origin", siteOrigin) // an exact origin, never "*" with cookies
        when {
            method == "OPTIONS" -> {                                               // a preflight
                if (allowed) {
                    ex.responseHeaders.set("Access-Control-Allow-Methods", "PUT")
                    ex.responseHeaders.set("Access-Control-Allow-Headers", "Content-Type")
                }
                send(ex, 204, null, "")
            }
            path == "/delete-account" -> send(ex, 200, "text/plain", "account deleted")
            else -> send(ex, 200, "application/json", """{"balance":1234}""")
        }
    }
    // The other origin's page: five fetch() calls, results written into the page.
    site.createContext("/") { ex -> send(ex, 200, "text/html", """
        <pre id=r></pre><script>
        const B = '$apiBase', out = [];
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
          document.getElementById('r').textContent = out.join('\n');
        })();
        </script>""") }
    api.start()
    site.start()
    try {
        val profile = Files.createTempDirectory("cors")
        val p = ProcessBuilder(chrome, "--headless=new", "--disable-gpu", "--no-first-run",
            "--user-data-dir=$profile", "--virtual-time-budget=5000", "--dump-dom", "$siteOrigin/")
            .redirectError(ProcessBuilder.Redirect.DISCARD).start()
        val dom = p.inputStream.readAllBytes().decodeToString().also { p.waitFor() }
        println("What the page's script got:")
        dom.substringAfter("<pre id=\"r\">").substringBefore("</pre>").lines()
            .forEach { println("  " + it.replace("&gt;", ">")) }                  // --dump-dom prints HTML, so ">" arrives escaped
        println("What the API server received:")
        log.forEach { println("  $it") }
    } finally {
        api.stop(0)
        site.stop(0)
    }
}
