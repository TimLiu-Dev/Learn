import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;
import java.util.stream.Collectors;

// SIX planted flaws. Find them before you read the fixed version.
public class VulnerableNotes {
    static final String API_KEY = "sk_live_9f3a2b7c";   // (6) hard-coded secret
    static Connection db;

    public static void main(String[] args) throws Exception {
        db = DriverManager.getConnection("jdbc:h2:mem:notes;DB_CLOSE_DELAY=-1");
        try (Statement st = db.createStatement()) {
            st.execute("CREATE TABLE users(name VARCHAR(30), password VARCHAR(64))");
            st.execute("CREATE TABLE notes(id INT, owner VARCHAR(30), body VARCHAR(400))");
            st.execute("INSERT INTO users VALUES ('ana','hunter2'),('ben','swordfish')");  // (1) plaintext passwords
            st.execute("INSERT INTO notes VALUES (1,'ana','Ana private note'),(2,'ben','<b>Ben</b> note'),(3,'ana','<script>steal()</script>')");
        }
        HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", Integer.parseInt(args[0])), 0);
        s.createContext("/login", VulnerableNotes::login);
        s.createContext("/notes/", VulnerableNotes::note);   // /notes/{id}
        s.start();
        System.out.println("up on " + s.getAddress().getPort());
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

    static void login(HttpExchange ex) {
        try {
            Map<String,String> p = query(ex);
            // (2) SQL built by concatenation
            String sql = "SELECT name FROM users WHERE name='" + p.get("name") + "' AND password='" + p.get("pw") + "'";
            try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                send(ex, 200, rs.next() ? "welcome " + rs.getString(1) : "no");
            }
        } catch (Exception e) {
            try { send(ex, 500, e.toString()); } catch (Exception ignored) {}   // (5) stack trace / details leak
        }
    }

    static void note(HttpExchange ex) {
        try {
            String id = ex.getRequestURI().getPath().substring("/notes/".length());
            // (4) no ownership check: any logged-in caller reads any note
            try (PreparedStatement ps = db.prepareStatement("SELECT body FROM notes WHERE id=?")) {
                ps.setInt(1, Integer.parseInt(id));
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { send(ex, 404, "no"); return; }
                    // (3) note echoed into HTML with no escaping
                    send(ex, 200, "<html><body>Note: " + rs.getString(1) + "</body></html>");
                }
            }
        } catch (Exception e) {
            try { send(ex, 500, e.toString()); } catch (Exception ignored) {}
        }
    }
}
