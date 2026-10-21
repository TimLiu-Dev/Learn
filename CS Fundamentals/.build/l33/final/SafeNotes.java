import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SafeNotes {
    // (6) secret comes from the environment, never the source
    static final String API_KEY = System.getenv().getOrDefault("NOTES_API_KEY", "dev-only");
    static final int ITER = 600_000;
    static final Map<String,String> SESSION = Map.of("tok-ana","ana","tok-ben","ben");
    static Connection db;

    public static void main(String[] args) throws Exception {
        db = DriverManager.getConnection("jdbc:h2:mem:notes;DB_CLOSE_DELAY=-1");
        try (Statement st = db.createStatement()) {
            st.execute("CREATE TABLE users(name VARCHAR(30), hash VARCHAR(200))");
            st.execute("CREATE TABLE notes(id INT, owner VARCHAR(30), body VARCHAR(400))");
            st.execute("INSERT INTO notes VALUES (1,'ana','Ana private note'),(2,'ben','<b>Ben</b> note'),(3,'ana','<script>steal()</script>')");
        }
        register("ana","hunter2"); register("ben","swordfish");  // (1) PBKDF2, not plaintext
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", Integer.parseInt(args[0])), 0);
        s.createContext("/login", SafeNotes::login);
        s.createContext("/notes/", SafeNotes::note);
        s.start();
        System.out.println("up on " + s.getAddress().getPort());
    }

    static byte[] pbkdf2(String pw, byte[] salt) throws Exception {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(new PBEKeySpec(pw.toCharArray(), salt, ITER, 256)).getEncoded();
    }

    static void register(String name, String pw) throws Exception {
        byte[] salt = new byte[16]; new SecureRandom().nextBytes(salt);
        String stored = ITER + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(pbkdf2(pw, salt));
        try (PreparedStatement ps = db.prepareStatement("INSERT INTO users VALUES (?,?)")) {   // (2) parameterized
            ps.setString(1, name); ps.setString(2, stored); ps.executeUpdate();
        }
    }

    static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#x27;");
    }

    static Map<String,String> query(HttpExchange ex) {
        String q = ex.getRequestURI().getRawQuery();
        if (q == null || q.isEmpty()) return Map.of();
        return java.util.Arrays.stream(q.split("&")).map(p -> p.split("=", 2))
            .collect(Collectors.toMap(p -> p[0], p -> URLDecoder.decode(p.length>1?p[1]:"", StandardCharsets.UTF_8)));
    }

    static void send(HttpExchange ex, int code, String body) throws Exception {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream o = ex.getResponseBody()) { o.write(b); }
    }

    // (5) Problem Details: a generic message to the client, the detail to the server log
    static void fail(HttpExchange ex, Exception e) {
        System.err.println("internal error: " + e);
        try { send(ex, 500, "{\"type\":\"about:blank\",\"title\":\"Internal Server Error\",\"status\":500}"); } catch (Exception ignored) {}
    }

    static void login(HttpExchange ex) {
        try {
            Map<String,String> p = query(ex);
            try (PreparedStatement ps = db.prepareStatement("SELECT hash FROM users WHERE name=?")) {  // (2)
                ps.setString(1, p.getOrDefault("name",""));
                try (ResultSet rs = ps.executeQuery()) {
                    boolean ok = false;
                    if (rs.next()) {
                        String[] parts = rs.getString(1).split(":");
                        byte[] salt = Base64.getDecoder().decode(parts[1]);
                        byte[] want = Base64.getDecoder().decode(parts[2]);
                        ok = MessageDigest.isEqual(pbkdf2(p.getOrDefault("pw",""), salt), want);  // constant time
                    }
                    send(ex, ok ? 200 : 401, ok ? "welcome " + p.get("name") : "no");
                }
            }
        } catch (Exception e) { fail(ex, e); }
    }

    static void note(HttpExchange ex) {
        try {
            String caller = SESSION.get(ex.getRequestHeaders().getFirst("X-Token"));
            if (caller == null) { send(ex, 401, "no"); return; }                 // authenticated?
            int id = Integer.parseInt(ex.getRequestURI().getPath().substring("/notes/".length()));
            try (PreparedStatement ps = db.prepareStatement("SELECT owner, body FROM notes WHERE id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    // (4) ownership check: not yours -> 404, don't even confirm it exists
                    if (!rs.next() || !rs.getString("owner").equals(caller)) { send(ex, 404, "no"); return; }
                    // (3) escape before putting user data in HTML
                    send(ex, 200, "<html><body>Note: " + escape(rs.getString("body")) + "</body></html>");
                }
            }
        } catch (Exception e) { fail(ex, e); }
    }
}
