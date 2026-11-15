import java.util.List;

public class PlanGrep {
    public static void main(String[] args) {
        List<String> plans = List.of(
            "SEARCH loans USING INDEX idx_loans_member (member_id=?)",
            "SCAN members USING COVERING INDEX sqlite_autoindex_members_1",
            "SCAN loans",
            "SEARCH b USING INTEGER PRIMARY KEY (rowid=?)");
        int good = 0;
        for (String p : plans) {
            if (p.contains("INDEX")) good++;   // "uses an index, so it's fast"?
        }
        System.out.println(good + " of " + plans.size() + " look fast");
    }
}
