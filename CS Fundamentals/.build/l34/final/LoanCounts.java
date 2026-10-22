import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanCounts {
    record MemberLoans(String name, int loans) {}

    static final String SQL = """
        SELECT m.name, count(l.id) AS loans
        FROM members m LEFT JOIN loans l ON l.member_id = m.id
        GROUP BY m.id
        ORDER BY loans DESC, m.name
        """;

    public static void main(String[] args) throws SQLException {
        List<MemberLoans> rows = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:library.db");
             PreparedStatement st = conn.prepareStatement(SQL);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                rows.add(new MemberLoans(rs.getString("name"), rs.getInt("loans")));
            }
        }
        int total = 0;
        for (MemberLoans r : rows) {
            System.out.printf("%-18s %d%n", r.name(), r.loans());
            total += r.loans();
        }
        System.out.println("members: " + rows.size() + ", total loans: " + total);
    }
}
