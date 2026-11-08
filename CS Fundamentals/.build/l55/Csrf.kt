import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.file.Files
import java.security.SecureRandom
import java.util.Base64
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

// A "bank" at http://127.0.0.1:<port> and an attacker's page at http://localhost:<port>.
// Different hosts, so to the browser they are different sites. A real headless Chrome is the victim.
val chrome = System.getenv("CHROME") ?: "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
val rng = SecureRandom()
val csrfOf = ConcurrentHashMap<String, String>()               // session id -> CSRF token
val log: MutableList<String> = Collections.synchronizedList(mutableListOf())
@Volatile var cookieAttrs = ""
@Volatile var defence = ""
var evilDelayMs = 0L                                           // "aged" mode: strike once the cookie is 2+ minutes old

fun token(): String = Base64.getUrlEncoder().withoutPadding().encodeToString(ByteArray(32).also { rng.nextBytes(it) })

fun reply(ex: HttpExchange, status: Int, html: String) {
    val bytes = html.toByteArray()
    ex.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
    ex.sendResponseHeaders(status, bytes.size.toLong())
    ex.responseBody.use { it.write(bytes) }
}

fun form(ex: HttpExchange): Map<String, String> =
    ex.requestBody.readAllBytes().decodeToString().split("&").filter { "=" in it }
        .associate { it.substringBefore("=") to URLDecoder.decode(it.substringAfter("="), "UTF-8") }

fun sessionId(ex: HttpExchange): String? =
    ex.requestHeaders.getFirst("Cookie")?.split(";")?.map { it.trim() }
        ?.firstOrNull { it.startsWith("sid=") }?.removePrefix("sid=")

fun main(args: Array<String>) {
    val bank = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    val evil = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
    val bankOrigin = "http://127.0.0.1:${bank.address.port}"

    // Ana logs in at the bank, then (in the same browser) visits the attacker's page.
    bank.createContext("/login") { ex ->
        val sid = token().also { csrfOf[it] = token() }
        ex.responseHeaders.add("Set-Cookie", "sid=$sid; HttpOnly; Path=/$cookieAttrs")
        ex.responseHeaders.set("Location", "http://localhost:${evil.address.port}/cute-cats")
        ex.sendResponseHeaders(302, -1)
        ex.close()
    }
    bank.createContext("/transfer") { ex ->
        val sid = sessionId(ex)
        val origin = ex.requestHeaders.getFirst("Origin")
        val f = form(ex)
        val (status, result) = when {
            sid == null || !csrfOf.containsKey(sid) -> 401 to "not logged in"
            defence == "token" && csrfOf[sid] != f["csrf"] -> 403 to "bad CSRF token"
            defence == "origin" && origin != bankOrigin -> 403 to "foreign Origin"
            else -> 200 to "SENT ${f["amount"]} to ${f["to"]}"
        }
        log += "cookie=%-3s Origin=%s Sec-Fetch-Site=%s".format(
            if (sid == null) "no" else "yes", origin, ex.requestHeaders.getFirst("Sec-Fetch-Site"))
        reply(ex, status, "<p id=r>$status $result</p>")
    }
    // The attacker's page: an invisible form that submits itself to the bank.
    evil.createContext("/cute-cats") { ex ->
        Thread.sleep(evilDelayMs)
        reply(ex, 200, """<form method=POST action='$bankOrigin/transfer'>""" +
            "<input name=to value=mallory><input name=amount value=100></form>" +
            "<script>document.forms[0].submit()</script>")
    }
    bank.start()
    evil.start()
    try {
        if (args.firstOrNull() == "aged") evilDelayMs = 125_000
        val cases = if (evilDelayMs > 0) listOf(Triple("(none), cookie 125 s old", "", ""))
        else listOf(
            Triple("(no SameSite attribute)", "", ""),
            Triple("SameSite=Lax", "; SameSite=Lax", ""),
            Triple("SameSite=Strict", "; SameSite=Strict", ""),
            Triple("(none) + CSRF token check", "", "token"),
            Triple("(none) + Origin check", "", "origin"),
        )
        for ((name, attrs, check) in cases) {
            cookieAttrs = attrs
            defence = check
            log.clear()
            val shown = victimBrowser("$bankOrigin/login").substringAfter("<p id=\"r\">").substringBefore("</p>")
            println("%-28s -> %s".format(name, shown))
            log.forEach { println("    bank saw: $it") }
        }
    } finally {
        bank.stop(0)
        evil.stop(0)
    }
}

// A fresh Chrome profile each time: no cookies left over from the last case.
fun victimBrowser(url: String): String {
    val profile = Files.createTempDirectory("victim")
    val p = ProcessBuilder(chrome, "--headless=new", "--disable-gpu", "--no-first-run",
        "--user-data-dir=$profile", "--virtual-time-budget=5000", "--dump-dom", url)
        .redirectError(ProcessBuilder.Redirect.DISCARD).start()
    return p.inputStream.readAllBytes().decodeToString().also { p.waitFor() }
}
