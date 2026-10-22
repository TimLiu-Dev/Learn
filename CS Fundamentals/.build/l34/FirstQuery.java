import java.sql.*;

public class FirstQuery {
    record Book(int id, String title, Integer year) {}

    public static void main(String[] args) throws SQLException {
        String sql = "SELECT id, title, year FROM books WHERE author_id = ? ORDER BY id";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:library.db");
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, 7);                                 // fills the ? (never concatenate: L33)
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int raw = rs.getInt("year");             // SQL NULL comes back as 0
                    Integer year = rs.wasNull() ? null : raw;
                    System.out.println(new Book(rs.getInt("id"), rs.getString("title"), year) + "  getInt=" + raw);
                }
            }
        }
    }
}
