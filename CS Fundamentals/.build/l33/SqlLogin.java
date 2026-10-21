import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlLogin {
    static Connection setUp() throws SQLException {
        Connection db = DriverManager.getConnection("jdbc:h2:mem:demo");  // in-memory, gone when the program exits
        try (Statement st = db.createStatement()) {
            st.execute("CREATE TABLE users(name VARCHAR(20), password VARCHAR(20))");
            st.execute("INSERT INTO users VALUES ('ana', 'p4ss'), ('ben', 'secret')");
        }
        return db;
    }

    // VULNERABLE: user input is pasted into the SQL text
    static boolean loginConcat(Connection db, String name, String pw) throws SQLException {
        String sql = "SELECT name FROM users WHERE name = '" + name + "' AND password = '" + pw + "'";
        System.out.println("  SQL: " + sql);
        try (Statement st = db.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    // FIXED: the SQL text is fixed; input travels separately as parameters
    static boolean loginPrepared(Connection db, String name, String pw) throws SQLException {
        try (PreparedStatement ps = db.prepareStatement("SELECT name FROM users WHERE name = ? AND password = ?")) {
            ps.setString(1, name);
            ps.setString(2, pw);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        try (Connection db = setUp()) {
            String evil = "' OR '1'='1";
            System.out.println("concat, right password:  " + loginConcat(db, "ana", "p4ss"));
            System.out.println("concat, wrong password:  " + loginConcat(db, "ana", "nope"));
            System.out.println("concat, crafted input:   " + loginConcat(db, "ana", evil));
            System.out.println("prepared, right password: " + loginPrepared(db, "ana", "p4ss"));
            System.out.println("prepared, crafted input:  " + loginPrepared(db, "ana", evil));
        }
    }
}
