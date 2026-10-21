import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.sql.Connection
import java.sql.DriverManager
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

const val ITER = 600_000
val SESSION = mapOf("tok-ana" to "ana", "tok-ben" to "ben")

// (6) secret from the environment, never the source
val apiKey: String = System.getenv("NOTES_API_KEY") ?: "dev-only"

lateinit var db: Connection

fun pbkdf2(pw: String, salt: ByteArray): ByteArray =
    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        .generateSecret(PBEKeySpec(pw.toCharArray(), salt, ITER, 256)).encoded

fun register(name: String, pw: String) {
    val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val stored = "$ITER:${Base64.getEncoder().encodeToString(salt)}:${Base64.getEncoder().encodeToString(pbkdf2(pw, salt))}"
    db.prepareStatement("INSERT INTO users VALUES (?,?)").use { ps ->   // (2) parameterized
        ps.setString(1, name); ps.setString(2, stored); ps.executeUpdate()
    }
}

fun escape(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    .replace("\"", "&quot;").replace("'", "&#x27;")

fun query(ex: HttpExchange): Map<String, String> =
    ex.requestURI.rawQuery?.split("&")?.associate {
        val kv = it.split("=", limit = 2)
        kv[0] to URLDecoder.decode(kv.getOrElse(1) { "" }, Charsets.UTF_8)
    } ?: emptyMap()

fun send(ex: HttpExchange, code: Int, body: String) {
    val b = body.toByteArray()
    ex.sendResponseHeaders(code, b.size.toLong())
    ex.responseBody.use { it.write(b) }
}

// (5) Problem Details out, detail to the log
fun fail(ex: HttpExchange, e: Exception) {
    System.err.println("internal error: $e")
    send(ex, 500, "{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500}")
}

fun login(ex: HttpExchange) = try {
    val p = query(ex)
    db.prepareStatement("SELECT hash FROM users WHERE name=?").use { ps ->
        ps.setString(1, p["name"] ?: "")
        ps.executeQuery().use { rs ->
            var ok = false
            if (rs.next()) {
                val (_, salt, want) = rs.getString(1).split(":")
                ok = MessageDigest.isEqual(pbkdf2(p["pw"] ?: "", Base64.getDecoder().decode(salt)), Base64.getDecoder().decode(want))
            }
            send(ex, if (ok) 200 else 401, if (ok) "welcome ${p["name"]}" else "no")
        }
    }
} catch (e: Exception) { fail(ex, e) }

fun note(ex: HttpExchange) = try {
    val caller = SESSION[ex.requestHeaders.getFirst("X-Token")]
    if (caller == null) send(ex, 401, "no")
    else {
        val id = ex.requestURI.path.removePrefix("/notes/").toInt()
        db.prepareStatement("SELECT owner, body FROM notes WHERE id=?").use { ps ->
            ps.setInt(1, id)
            ps.executeQuery().use { rs ->
                if (!rs.next() || rs.getString("owner") != caller) send(ex, 404, "no")   // (4) ownership
                else send(ex, 200, "<html><body>Note: ${escape(rs.getString("body"))}</body></html>")  // (3) escape
            }
        }
    }
} catch (e: Exception) { fail(ex, e) }

fun main(args: Array<String>) {
    db = DriverManager.getConnection("jdbc:h2:mem:notes;DB_CLOSE_DELAY=-1")
    db.createStatement().use { st ->
        st.execute("CREATE TABLE users(name VARCHAR(30), hash VARCHAR(200))")
        st.execute("CREATE TABLE notes(id INT, owner VARCHAR(30), body VARCHAR(400))")
        st.execute("INSERT INTO notes VALUES (1,'ana','Ana private note'),(2,'ben','<b>Ben</b> note'),(3,'ana','<script>steal()</script>')")
    }
    register("ana", "hunter2"); register("ben", "swordfish")   // (1) PBKDF2
    val s = HttpServer.create(InetSocketAddress("127.0.0.1", args[0].toInt()), 0)
    s.createContext("/login", ::login)
    s.createContext("/notes/", ::note)
    s.start()
    println("up on ${s.address.port}")
}
