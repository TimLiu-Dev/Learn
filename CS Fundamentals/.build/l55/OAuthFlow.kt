import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put

// The authorization-code flow with PKCE (RFC 6749 §4.1 + RFC 7636), with all three parties on 127.0.0.1:
//   AS = authorization server, APP = the client ("photo-app"), API = the resource server (photos).
val rng = SecureRandom()
val b64 = Base64.getUrlEncoder().withoutPadding()
val unb64 = Base64.getUrlDecoder()
fun random() = b64.encodeToString(ByteArray(32).also { rng.nextBytes(it) })
fun sha256b64(s: String) = b64.encodeToString(MessageDigest.getInstance("SHA-256").digest(s.toByteArray()))
fun enc(s: String) = URLEncoder.encode(s, "UTF-8")
fun query(s: String?): Map<String, String> = s.orEmpty().split("&").filter { "=" in it }
    .associate { it.substringBefore("=") to URLDecoder.decode(it.substringAfter("="), "UTF-8") }

// ---- tiny HS256 JWTs (Part 2), signed by the AS; the API shares the key for simplicity ----
val asKey = ByteArray(32).also { rng.nextBytes(it) }
fun hmac(input: String) = Mac.getInstance("HmacSHA256").run { init(SecretKeySpec(asKey, "HmacSHA256")); doFinal(input.toByteArray()) }
fun jwt(claims: JsonObject): String {
    val input = b64.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray()) + "." + b64.encodeToString(claims.toString().toByteArray())
    return input + "." + b64.encodeToString(hmac(input))
}
fun verifiedClaims(token: String): JsonObject? {
    val p = token.split(".")
    if (p.size != 3 || !MessageDigest.isEqual(hmac("${p[0]}.${p[1]}"), runCatching { unb64.decode(p[2]) }.getOrNull())) return null
    return Json.parseToJsonElement(unb64.decode(p[1]).decodeToString()).jsonObject
}
fun now() = System.currentTimeMillis() / 1000

fun send(ex: HttpExchange, status: Int, body: String, type: String = "text/plain") {
    val bytes = body.toByteArray()
    ex.responseHeaders.set("Content-Type", type)
    ex.sendResponseHeaders(status, if (bytes.isEmpty()) -1 else bytes.size.toLong())
    if (bytes.isNotEmpty()) ex.responseBody.use { it.write(bytes) }
    ex.close()
}
fun redirect(ex: HttpExchange, to: String) { ex.responseHeaders.set("Location", to); ex.sendResponseHeaders(302, -1); ex.close() }
fun server() = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)

class Code(val clientId: String, val redirectUri: String, val challenge: String, val nonce: String, val expires: Long, var used: Boolean = false)

fun main() {
    val asServer = server(); val app = server(); val api = server()
    val AS = "http://127.0.0.1:${asServer.address.port}"
    val APP = "http://127.0.0.1:${app.address.port}"
    val API = "http://127.0.0.1:${api.address.port}"
    val http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build()

    // ================= AUTHORIZATION SERVER =================
    val registered = mapOf("photo-app" to "$APP/callback")                // exact redirect URIs (RFC 9700 §2.1)
    val codes = ConcurrentHashMap<String, Code>()
    asServer.createContext("/authorize") { ex ->
        val q = query(ex.requestURI.rawQuery)
        val expected = registered[q["client_id"]]
        if (expected == null || expected != q["redirect_uri"])           // never redirect to an unknown place
            return@createContext send(ex, 400, "unregistered client or redirect_uri")
        if (q["code_challenge_method"] != "S256" || q["code_challenge"].isNullOrEmpty())
            return@createContext send(ex, 400, "PKCE S256 required")
        // (A real AS now shows a login page and a consent screen; here ana is logged in and clicks "Allow".)
        val code = random()
        codes[code] = Code(q.getValue("client_id"), expected, q.getValue("code_challenge"), q["nonce"].orEmpty(), now() + 60)
        redirect(ex, "$expected?code=${enc(code)}&state=${enc(q["state"].orEmpty())}")
    }
    asServer.createContext("/token") { ex ->
        val f = query(ex.requestBody.readAllBytes().decodeToString())
        val c = f["code"]?.let { codes[it] }
        val fail = when {
            c == null -> "unknown code"
            c.used -> "code already used"                                // single use (RFC 6749 §4.1.2)
            now() > c.expires -> "code expired"
            c.clientId != f["client_id"] || c.redirectUri != f["redirect_uri"] -> "client or redirect_uri mismatch"
            sha256b64(f["code_verifier"].orEmpty()) != c.challenge -> "PKCE verifier does not match"
            else -> null
        }
        c?.used = true
        if (fail != null) return@createContext send(ex, 400, """{"error":"invalid_grant","error_description":"$fail"}""", "application/json")
        val access = jwt(buildJsonObject {
            put("iss", AS); put("sub", "ana"); put("aud", "photos-api"); put("scope", "photos.read"); put("exp", now() + 900)
        })
        val id = jwt(buildJsonObject {                                  // OIDC: who logged in, for the APP to read
            put("iss", AS); put("sub", "ana"); put("aud", c!!.clientId); put("nonce", c.nonce); put("exp", now() + 300)
        })
        send(ex, 200, buildJsonObject {
            put("access_token", access); put("token_type", "Bearer"); put("expires_in", 900); put("id_token", id)
        }.toString(), "application/json")
    }

    // ================= RESOURCE SERVER (photos API) =================
    api.createContext("/photos") { ex ->
        val token = ex.requestHeaders.getFirst("Authorization")?.removePrefix("Bearer ")
        val c = token?.let { verifiedClaims(it) }
        val problem = when {
            c == null -> "invalid_token"
            c["aud"]?.jsonPrimitive?.content != "photos-api" -> "invalid_token\", error_description=\"wrong audience"
            c["exp"]!!.jsonPrimitive.long < now() -> "invalid_token\", error_description=\"expired"
            c["scope"]?.jsonPrimitive?.content?.split(" ")?.contains("photos.read") != true -> "insufficient_scope"
            else -> null
        }
        if (problem != null) {
            ex.responseHeaders.set("WWW-Authenticate", "Bearer error=\"$problem\"")   // RFC 6750 §3
            return@createContext send(ex, if (problem == "insufficient_scope") 403 else 401, "")
        }
        send(ex, 200, """["beach.jpg","cat.jpg"] for ${c!!["sub"]!!.jsonPrimitive.content}""")
    }

    // ================= CLIENT APP =================
    class Pending(val verifier: String, val nonce: String)
    val pending = ConcurrentHashMap<String, Pending>()                    // state -> what this browser started
    app.createContext("/login") { ex ->
        val state = random(); val verifier = random(); val nonce = random()
        pending[state] = Pending(verifier, nonce)
        redirect(ex, "$AS/authorize?response_type=code&client_id=photo-app&redirect_uri=${enc("$APP/callback")}" +
            "&scope=${enc("openid photos.read")}&state=$state&nonce=$nonce" +
            "&code_challenge=${sha256b64(verifier)}&code_challenge_method=S256")
    }
    app.createContext("/callback") { ex ->
        val q = query(ex.requestURI.rawQuery)
        val p = q["state"]?.let { pending.remove(it) }
            ?: return@createContext send(ex, 400, "unknown state: I never started this login")   // CSRF on the callback
        val tokenRes = http.send(HttpRequest.newBuilder(URI("$AS/token"))         // back channel: browser never sees it
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=${enc(q["code"].orEmpty())}" +
                "&redirect_uri=${enc("$APP/callback")}&client_id=photo-app&code_verifier=${p.verifier}")).build(),
            HttpResponse.BodyHandlers.ofString())
        if (tokenRes.statusCode() != 200) return@createContext send(ex, 502, "token exchange failed: ${tokenRes.body()}")
        val tokens = Json.parseToJsonElement(tokenRes.body()).jsonObject
        val idClaims = verifiedClaims(tokens["id_token"]!!.jsonPrimitive.content)!!
        if (idClaims["aud"]?.jsonPrimitive?.content != "photo-app" || idClaims["nonce"]?.jsonPrimitive?.content != p.nonce)
            return@createContext send(ex, 400, "bad ID token")
        val photos = http.send(HttpRequest.newBuilder(URI("$API/photos"))
            .header("Authorization", "Bearer ${tokens["access_token"]!!.jsonPrimitive.content}").build(),
            HttpResponse.BodyHandlers.ofString())
        send(ex, 200, "hello ${idClaims["sub"]!!.jsonPrimitive.content}! photos: ${photos.body()}")
    }

    listOf(asServer, app, api).forEach { it.start() }
    try {
        // ---- the browser: follow each redirect by hand so every hop is visible ----
        fun get(url: String) = http.send(HttpRequest.newBuilder(URI(url)).build(), HttpResponse.BodyHandlers.ofString())
        fun short(url: String) = url.replace(enc(APP), "APP").replace(AS, "AS").replace(APP, "APP").replace(API, "API")
            .replace(Regex("=([A-Za-z0-9_-]{12})[A-Za-z0-9_%-]{20,}"), "=$1…")
        println("== the happy path ==")
        var url = "$APP/login"
        var lastCode = ""
        while (true) {
            val r = get(url)
            val loc = r.headers().firstValue("Location").orElse(null)
            println("browser GET ${short(url)}\n   -> ${r.statusCode()} ${loc?.let { "Location: " + short(it) } ?: r.body()}")
            if (loc == null) break
            query(URI(loc).rawQuery)["code"]?.let { lastCode = it }
            url = loc
        }

        println("== the checks ==")
        fun check(label: String, result: String) = println("${label.padEnd(35)}-> $result")
        fun token(code: String, verifier: String, redirect: String = "$APP/callback") = http.send(
            HttpRequest.newBuilder(URI("$AS/token")).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=${enc(code)}" +
                    "&redirect_uri=${enc(redirect)}&client_id=photo-app&code_verifier=$verifier")).build(),
            HttpResponse.BodyHandlers.ofString()).let { "${it.statusCode()} ${it.body()}" }
        check("1 the same code again", token(lastCode, "anything"))
        // a fresh code, issued for a challenge whose verifier only the real app knows
        val realVerifier = random()
        val loc = get("$AS/authorize?response_type=code&client_id=photo-app&redirect_uri=${enc("$APP/callback")}" +
            "&state=s1&code_challenge=${sha256b64(realVerifier)}&code_challenge_method=S256").headers().firstValue("Location").get()
        val code = query(URI(loc).rawQuery).getValue("code")
        check("2 a copied code, no verifier", token(code, random()))
        val bad = get("$AS/authorize?response_type=code&client_id=photo-app&redirect_uri=${enc("https://evil.example/cb")}" +
            "&state=s2&code_challenge=${sha256b64(realVerifier)}&code_challenge_method=S256")
        check("3 an unregistered redirect_uri", "${bad.statusCode()} ${bad.body()}")
        val forged = get("$APP/callback?code=whatever&state=not-mine")
        check("4 a callback the app didn't start", "${forged.statusCode()} ${forged.body()}")
        val id = jwt(buildJsonObject { put("iss", AS); put("sub", "ana"); put("aud", "photo-app"); put("exp", now() + 300) })
        val misuse = http.send(HttpRequest.newBuilder(URI("$API/photos")).header("Authorization", "Bearer $id").build(),
            HttpResponse.BodyHandlers.ofString())
        check("5 an ID token sent to the API", "${misuse.statusCode()} WWW-Authenticate: ${misuse.headers().firstValue("WWW-Authenticate").get()}")
    } finally {
        listOf(asServer, app, api).forEach { it.stop(0) }
    }
}
