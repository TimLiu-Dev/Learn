import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// A private HTTP cache in front of HttpClient. Start EtagServer first (port 18132).
data class Entry(val body: String, val etag: String?, val freshUntil: Long)

val http: HttpClient = HttpClient.newHttpClient()
val cache = object : LinkedHashMap<String, Entry>(16, 0.75f, true) {     // L19's LRU
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Entry>) = size > 100
}
var originCalls = 0; var hits = 0; var revalidated = 0

// Cache-Control: max-age=2  ->  fresh for 2 s;  no-cache  ->  stale at once;  no-store  ->  never stored
fun freshUntil(r: HttpResponse<*>, now: Long): Long {
    val cc = r.headers().firstValue("Cache-Control").orElse("")
    if ("no-cache" in cc) return now
    val maxAge = cc.split(",").map { it.trim() }.firstOrNull { it.startsWith("max-age=") }
    return if (maxAge == null) now else now + 1000L * maxAge.removePrefix("max-age=").toInt()
}

fun get(path: String): String {
    val now = System.currentTimeMillis()
    val e = cache[path]
    if (e != null && now < e.freshUntil) {                 // fresh: no network at all
        hits++
        return "HIT          ${e.body.trim()}"
    }
    val req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:18132$path"))
    e?.etag?.let { req.header("If-None-Match", it) }        // stale: ask "changed?"
    val r = http.send(req.build(), HttpResponse.BodyHandlers.ofString())
    originCalls++
    if (r.statusCode() == 304 && e != null) {               // "not modified": reuse our copy
        revalidated++
        cache[path] = e.copy(freshUntil = freshUntil(r, now))
        return "REVALIDATED  ${e.body.trim()}"
    }
    val cc = r.headers().firstValue("Cache-Control").orElse("")
    if ("no-store" in cc) return "MISS         ${r.body().trim()}   (no-store: not kept)"
    cache[path] = Entry(r.body(), r.headers().firstValue("ETag").orElse(null), freshUntil(r, now))
    return "MISS         ${r.body().trim()}"
}

fun main() {
    val script = listOf("/time", "/time", "/time", "sleep", "/time",
                        "/report", "/report", "/report", "/secret", "/secret")
    for (step in script) {
        if (step == "sleep") { Thread.sleep(2100); println("... 2.1 s later"); continue }
        println("GET %-8s %s".format(step, get(step)))
    }
    println("origin calls: $originCalls   served from cache with no call: $hits   revalidated (304): $revalidated")
}
