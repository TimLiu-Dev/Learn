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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

// The authorization-code flow with PKCE (RFC 6749 §4.1 + RFC 7636), with all three parties on 127.0.0.1:
//   AS = authorization server, APP = the client ("photo-app"), API = the resource server (photos).
public class OAuthFlow {
    static final SecureRandom rng = new SecureRandom();
    static final Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
    static final Base64.Decoder unb64 = Base64.getUrlDecoder();
    static final JsonMapper json = JsonMapper.builder().build();

    static String random() { var b = new byte[32]; rng.nextBytes(b); return b64.encodeToString(b); }
    static String sha256b64(String s) {
        try { return b64.encodeToString(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.US_ASCII))); }
        catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }
    static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    static Map<String, String> query(String s) {
        var m = new HashMap<String, String>();
        if (s == null) return m;
        for (var kv : s.split("&")) {
            int i = kv.indexOf('=');
            if (i > 0) m.put(kv.substring(0, i), URLDecoder.decode(kv.substring(i + 1), StandardCharsets.UTF_8));
        }
        return m;
    }

    // ---- tiny HS256 JWTs (Part 2), signed by the AS; the API shares the key for simplicity ----
    static final byte[] asKey = new byte[32];
    static { rng.nextBytes(asKey); }
    static byte[] hmac(String input) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(asKey, "HmacSHA256"));
            return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) { throw new IllegalStateException(e); }
    }
    static String jwt(ObjectNode claims) {
        var input = b64.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + b64.encodeToString(claims.toString().getBytes(StandardCharsets.UTF_8));
        return input + "." + b64.encodeToString(hmac(input));
    }
    static JsonNode verifiedClaims(String token) {
        var p = token.split("\\.", -1);
        if (p.length != 3) return null;
        try {
            if (!MessageDigest.isEqual(hmac(p[0] + "." + p[1]), unb64.decode(p[2]))) return null;
        } catch (IllegalArgumentException e) { return null; }
        return json.readTree(new String(unb64.decode(p[1]), StandardCharsets.UTF_8));
    }
    static long now() { return System.currentTimeMillis() / 1000; }

    static void send(HttpExchange ex, int status, String body, String type) throws IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) try (var out = ex.getResponseBody()) { out.write(bytes); }
        ex.close();
    }
    static void send(HttpExchange ex, int status, String body) throws IOException { send(ex, status, body, "text/plain"); }
    static void redirect(HttpExchange ex, String to) throws IOException {
        ex.getResponseHeaders().set("Location", to);
        ex.sendResponseHeaders(302, -1);
        ex.close();
    }
    static HttpServer server() throws IOException { return HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0); }

    static final class Code {
        final String clientId, redirectUri, challenge, nonce; final long expires; boolean used;
        Code(String clientId, String redirectUri, String challenge, String nonce, long expires) {
            this.clientId = clientId; this.redirectUri = redirectUri; this.challenge = challenge; this.nonce = nonce; this.expires = expires;
        }
    }
    record Pending(String verifier, String nonce) {}

    public static void main(String[] args) throws Exception {
        var asServer = server(); var app = server(); var api = server();
        String AS = "http://127.0.0.1:" + asServer.getAddress().getPort();
        String APP = "http://127.0.0.1:" + app.getAddress().getPort();
        String API = "http://127.0.0.1:" + api.getAddress().getPort();
        var http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();

        // ================= AUTHORIZATION SERVER =================
        var registered = Map.of("photo-app", APP + "/callback");           // exact redirect URIs (RFC 9700 §2.1)
        var codes = new ConcurrentHashMap<String, Code>();
        asServer.createContext("/authorize", ex -> {
            var q = query(ex.getRequestURI().getRawQuery());
            var clientId = q.get("client_id");
            var expected = clientId == null ? null : registered.get(clientId);
            if (expected == null || !expected.equals(q.get("redirect_uri"))) {   // never redirect to an unknown place
                send(ex, 400, "unregistered client or redirect_uri"); return;
            }
            if (!"S256".equals(q.get("code_challenge_method")) || q.getOrDefault("code_challenge", "").isEmpty()) {
                send(ex, 400, "PKCE S256 required"); return;
            }
            // (A real AS now shows a login page and a consent screen; here ana is logged in and clicks "Allow".)
            var code = random();
            codes.put(code, new Code(clientId, expected, q.get("code_challenge"), q.getOrDefault("nonce", ""), now() + 60));
            redirect(ex, expected + "?code=" + enc(code) + "&state=" + enc(q.getOrDefault("state", "")));
        });
        asServer.createContext("/token", ex -> {
            var f = query(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            var c = f.get("code") == null ? null : codes.get(f.get("code"));
            String fail = null;
            if (c == null) fail = "unknown code";
            else if (c.used) fail = "code already used";                   // single use (RFC 6749 §4.1.2)
            else if (now() > c.expires) fail = "code expired";
            else if (!c.clientId.equals(f.get("client_id")) || !c.redirectUri.equals(f.get("redirect_uri"))) fail = "client or redirect_uri mismatch";
            else if (!sha256b64(f.getOrDefault("code_verifier", "")).equals(c.challenge)) fail = "PKCE verifier does not match";
            if (c != null) c.used = true;
            if (fail != null) {
                send(ex, 400, "{\"error\":\"invalid_grant\",\"error_description\":\"" + fail + "\"}", "application/json"); return;
            }
            var access = jwt(json.createObjectNode().put("iss", AS).put("sub", "ana").put("aud", "photos-api")
                    .put("scope", "photos.read").put("exp", now() + 900));
            var id = jwt(json.createObjectNode().put("iss", AS).put("sub", "ana")      // OIDC: who logged in, for the APP to read
                    .put("aud", c.clientId).put("nonce", c.nonce).put("exp", now() + 300));
            send(ex, 200, json.createObjectNode().put("access_token", access).put("token_type", "Bearer")
                    .put("expires_in", 900).put("id_token", id).toString(), "application/json");
        });

        // ================= RESOURCE SERVER (photos API) =================
        api.createContext("/photos", ex -> {
            var auth = ex.getRequestHeaders().getFirst("Authorization");
            var c = auth == null || !auth.startsWith("Bearer ") ? null : verifiedClaims(auth.substring(7));
            String problem = null;
            if (c == null) problem = "invalid_token";
            else if (!"photos-api".equals(c.path("aud").asString(null))) problem = "invalid_token\", error_description=\"wrong audience";
            else if (c.path("exp").asLong() < now()) problem = "invalid_token\", error_description=\"expired";
            else if (!Arrays.asList(c.path("scope").asString("").split(" ")).contains("photos.read")) problem = "insufficient_scope";
            if (problem != null) {
                ex.getResponseHeaders().set("WWW-Authenticate", "Bearer error=\"" + problem + "\"");   // RFC 6750 §3
                send(ex, problem.equals("insufficient_scope") ? 403 : 401, ""); return;
            }
            send(ex, 200, "[\"beach.jpg\",\"cat.jpg\"] for " + c.path("sub").asString());
        });

        // ================= CLIENT APP =================
        var pending = new ConcurrentHashMap<String, Pending>();            // state -> what this browser started
        app.createContext("/login", ex -> {
            String state = random(), verifier = random(), nonce = random();
            pending.put(state, new Pending(verifier, nonce));
            redirect(ex, AS + "/authorize?response_type=code&client_id=photo-app&redirect_uri=" + enc(APP + "/callback")
                    + "&scope=" + enc("openid photos.read") + "&state=" + state + "&nonce=" + nonce
                    + "&code_challenge=" + sha256b64(verifier) + "&code_challenge_method=S256");
        });
        app.createContext("/callback", ex -> {
            var q = query(ex.getRequestURI().getRawQuery());
            var p = q.get("state") == null ? null : pending.remove(q.get("state"));
            if (p == null) { send(ex, 400, "unknown state: I never started this login"); return; }   // CSRF on the callback
            try {
                var tokenRes = http.send(HttpRequest.newBuilder(URI.create(AS + "/token"))   // back channel: browser never sees it
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + enc(q.getOrDefault("code", ""))
                                + "&redirect_uri=" + enc(APP + "/callback") + "&client_id=photo-app&code_verifier=" + p.verifier())).build(),
                        HttpResponse.BodyHandlers.ofString());
                if (tokenRes.statusCode() != 200) { send(ex, 502, "token exchange failed: " + tokenRes.body()); return; }
                var tokens = json.readTree(tokenRes.body());
                var idClaims = verifiedClaims(tokens.path("id_token").asString());
                if (idClaims == null || !"photo-app".equals(idClaims.path("aud").asString(null))
                        || !p.nonce().equals(idClaims.path("nonce").asString(null))) { send(ex, 400, "bad ID token"); return; }
                var photos = http.send(HttpRequest.newBuilder(URI.create(API + "/photos"))
                        .header("Authorization", "Bearer " + tokens.path("access_token").asString()).build(),
                        HttpResponse.BodyHandlers.ofString());
                send(ex, 200, "hello " + idClaims.path("sub").asString() + "! photos: " + photos.body());
            } catch (InterruptedException e) { throw new IOException(e); }
        });

        for (var s : List.of(asServer, app, api)) s.start();
        try {
            System.out.println("== the happy path ==");
            String url = APP + "/login", lastCode = "";
            while (true) {
                var r = get(http, url);
                var loc = r.headers().firstValue("Location").orElse(null);
                System.out.println("browser GET " + shortUrl(url, AS, APP, API) + "\n   -> " + r.statusCode() + " "
                        + (loc != null ? "Location: " + shortUrl(loc, AS, APP, API) : r.body()));
                if (loc == null) break;
                var code = query(URI.create(loc).getRawQuery()).get("code");
                if (code != null) lastCode = code;
                url = loc;
            }

            System.out.println("== the checks ==");
            check("1 the same code again", token(http, AS, APP, lastCode, "anything"));
            // a fresh code, issued for a challenge whose verifier only the real app knows
            var realVerifier = random();
            var loc = get(http, AS + "/authorize?response_type=code&client_id=photo-app&redirect_uri=" + enc(APP + "/callback")
                    + "&state=s1&code_challenge=" + sha256b64(realVerifier) + "&code_challenge_method=S256").headers().firstValue("Location").orElseThrow();
            var code = query(URI.create(loc).getRawQuery()).get("code");
            check("2 a copied code, no verifier", token(http, AS, APP, code, random()));
            var bad = get(http, AS + "/authorize?response_type=code&client_id=photo-app&redirect_uri=" + enc("https://evil.example/cb")
                    + "&state=s2&code_challenge=" + sha256b64(realVerifier) + "&code_challenge_method=S256");
            check("3 an unregistered redirect_uri", bad.statusCode() + " " + bad.body());
            var forged = get(http, APP + "/callback?code=whatever&state=not-mine");
            check("4 a callback the app didn't start", forged.statusCode() + " " + forged.body());
            var id = jwt(json.createObjectNode().put("iss", AS).put("sub", "ana").put("aud", "photo-app").put("exp", now() + 300));
            var misuse = http.send(HttpRequest.newBuilder(URI.create(API + "/photos")).header("Authorization", "Bearer " + id).build(),
                    HttpResponse.BodyHandlers.ofString());
            check("5 an ID token sent to the API", misuse.statusCode() + " WWW-Authenticate: " + misuse.headers().firstValue("WWW-Authenticate").orElseThrow());
        } finally {
            for (var s : List.of(asServer, app, api)) s.stop(0);
        }
    }

    static void check(String label, String result) { System.out.println(String.format("%-35s", label) + "-> " + result); }

    static HttpResponse<String> get(HttpClient http, String url) throws Exception {
        return http.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString());
    }
    static String token(HttpClient http, String AS, String APP, String code, String verifier) throws Exception {
        var r = http.send(HttpRequest.newBuilder(URI.create(AS + "/token")).header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + enc(code)
                        + "&redirect_uri=" + enc(APP + "/callback") + "&client_id=photo-app&code_verifier=" + verifier)).build(),
                HttpResponse.BodyHandlers.ofString());
        return r.statusCode() + " " + r.body();
    }
    static String shortUrl(String url, String AS, String APP, String API) {
        return url.replace(enc(APP), "APP").replace(AS, "AS").replace(APP, "APP").replace(API, "API")
                .replaceAll("=([A-Za-z0-9_-]{12})[A-Za-z0-9_%-]{20,}", "=$1…");
    }
}
