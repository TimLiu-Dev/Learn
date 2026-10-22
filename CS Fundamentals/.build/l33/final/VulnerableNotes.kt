import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.sql.Connection
import java.sql.DriverManager

// SIX planted flaws. Find them before you read the fixed version.
const val API_KEY = "sk_live_9f3a2b7c"   // (6) hard-coded secret

lateinit var db: Connection

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

fun login(ex: HttpExchange) = try {
    val p = query(ex)
    // (2) SQL built by concatenation
    val sql = "SELECT name FROM users WHERE name='${p["name"]}' AND password='${p["pw"]}'"
    db.createStatement().use { st ->
        st.executeQuery(sql).use { rs -> send(ex, 200, if (rs.next()) "welcome ${rs.getString(1)}" else "no") }
    }
} catch (e: Exception) { send(ex, 500, e.toString()) }   // (5) internals leak to the client

fun note(ex: HttpExchange) = try {
    val id = ex.requestURI.path.removePrefix("/notes/").toInt()
    // (4) no ownership check: any caller reads any note
    db.prepareStatement("SELECT body FROM notes WHERE id=?").use { ps ->
        ps.setInt(1, id)
        ps.executeQuery().use { rs ->
            if (!rs.next()) send(ex, 404, "no")
            else send(ex, 200, "<html><body>Note: ${rs.getString(1)}</body></html>")   // (3) no escaping
        }
    }
} catch (e: Exception) { send(ex, 500, e.toString()) }

fun main(args: Array<String>) {
    db = DriverManager.getConnection("jdbc:h2:mem:notes;DB_CLOSE_DELAY=-1")
    db.createStatement().use { st ->
        st.execute("CREATE TABLE users(name VARCHAR(30), password VARCHAR(64))")
        st.execute("CREATE TABLE notes(id INT, owner VARCHAR(30), body VARCHAR(400))")
        st.execute("INSERT INTO users VALUES ('ana','hunter2'),('ben','swordfish')")   // (1) plaintext passwords
        st.execute("INSERT INTO notes VALUES (1,'ana','Ana private note'),(2,'ben','<b>Ben</b> note'),(3,'ana','<script>steal()</script>')")
    }
    val s = HttpServer.create(InetSocketAddress("127.0.0.1", args[0].toInt()), 0)
    s.createContext("/login", ::login)
    s.createContext("/notes/", ::note)
    s.start()
    println("up on ${s.address.port}")
}
