import java.sql.*;

public class GetObjectNull {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:library.db");
             ResultSet rs = conn.createStatement().executeQuery("SELECT year FROM books WHERE id = 19")) {
            rs.next();
            System.out.println(rs.getObject("year", Integer.class));
        }
    }
}
