import java.util.*;

public class Practice {
    // What a PreparedStatement needs: SQL text with ? placeholders, and the values in the same order.
    record Query(String sql, List<Object> params) { }

    // The only column names that may ever reach the SQL text.
    static final Set<String> UPDATABLE_COLUMNS = Set.of("name", "email");

    // PATCH /members/{id}: the client sends only the fields it wants to change,
    // e.g. {"email": "ada@example.org"}. The keys come from the client too.
    static Query updateMember(int id, Map<String, Object> changes) {
        if (changes.isEmpty()) throw new IllegalArgumentException("nothing to update");
        StringJoiner sets = new StringJoiner(", ");
        List<Object> params = new ArrayList<>(); // ArrayList, because List.of rejects null
        for (Map.Entry<String, Object> e : changes.entrySet()) {
            if (!UPDATABLE_COLUMNS.contains(e.getKey())) {
                throw new IllegalArgumentException("can't update '" + e.getKey() + "'");
            }
            // Column names: our own strings, checked above. Values: always placeholders.
            sets.add(e.getKey() + " = ?");
            params.add(e.getValue());
        }
        params.add(id);
        return new Query("UPDATE members SET " + sets + " WHERE id = ?", params);
    }

    // CHECKS
    static Map<String, Object> changes(Object... keysAndValues) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) m.put((String) keysAndValues[i], keysAndValues[i + 1]);
        return m;
    }

    static String rejected(Map<String, Object> changes) {
        try {
            return "not rejected: " + updateMember(7, changes);
        } catch (IllegalArgumentException e) {
            return "rejected";
        }
    }

    public static void main(String[] args) {
        check("one field gives one placeholder",
            new Query("UPDATE members SET email = ? WHERE id = ?", Arrays.asList("ada@example.org", 7)),
            () -> updateMember(7, changes("email", "ada@example.org")));
        check("fields keep the caller's order and the id comes last",
            new Query("UPDATE members SET name = ?, email = ? WHERE id = ?", Arrays.asList("Ada", "ada@example.org", 7)),
            () -> updateMember(7, changes("name", "Ada", "email", "ada@example.org")));
        check("a quote in a value never reaches the SQL text",
            new Query("UPDATE members SET name = ? WHERE id = ?", Arrays.asList("x' OR '1'='1", 7)),
            () -> updateMember(7, changes("name", "x' OR '1'='1")));
        check("a column outside the allowlist is rejected", "rejected", () -> rejected(changes("is_admin", 1)));
        check("a glued-in key is rejected too", "rejected", () -> rejected(changes("name = 'x', email", "y")));
        check("null is a value too",
            new Query("UPDATE members SET email = ? WHERE id = ?", Arrays.asList(null, 7)),
            () -> updateMember(7, changes("email", null)));
        check("an empty change is refused", "rejected", () -> rejected(changes()));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
