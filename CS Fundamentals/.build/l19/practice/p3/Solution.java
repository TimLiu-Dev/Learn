import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    record Member(String name, String city) {}     // a record's fields are final: its hash can never change

    static class Registry {
        private final Map<Member, Integer> points = new HashMap<>();

        int size() { return points.size(); }
        void add(Member member, int pts) { points.put(member, pts); }
        Integer pointsOf(Member member) { return points.get(member); }

        // Gives the member a new name and keeps their points. Returns the renamed member, or null if unknown.
        Member rename(Member member, String newName) {
            Integer pts = points.remove(member);   // take the entry out under its old hash
            if (pts == null) return null;
            Member renamed = new Member(newName, member.city());
            points.put(renamed, pts);              // and file it again under the new one
            return renamed;
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("the points follow the new name", 100, () -> {
            Registry r = new Registry();
            Member ada = new Member("ada", "London");
            r.add(ada, 100);
            r.rename(ada, "ada lovelace");
            return r.pointsOf(new Member("ada lovelace", "London"));
        });
        check("the old name is gone", "null, size 1", () -> {
            Registry r = new Registry();
            Member ada = new Member("ada", "London");
            r.add(ada, 100);
            r.rename(ada, "ada lovelace");
            return r.pointsOf(new Member("ada", "London")) + ", size " + r.size();
        });
        check("rename returns the new key", new Member("ada lovelace", "London"), () -> {
            Registry r = new Registry();
            r.add(new Member("ada", "London"), 100);
            return r.rename(new Member("ada", "London"), "ada lovelace");
        });
        check("the caller's member object is left alone", "ada", () -> {
            Registry r = new Registry();
            Member ada = new Member("ada", "London");
            r.add(ada, 100);
            r.rename(ada, "ada lovelace");
            return ada.name();
        });
        check("an unknown member is not renamed", "null, size 1, ada has 100", () -> {
            Registry r = new Registry();
            r.add(new Member("ada", "London"), 100);
            Member renamed = r.rename(new Member("bob", "Paris"), "rob");
            return renamed + ", size " + r.size() + ", ada has " + r.pointsOf(new Member("ada", "London"));
        });
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
