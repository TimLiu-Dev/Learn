import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    static final class Member {
        private String name;                       // mutable: the hash changes with it
        private final String city;

        Member(String name, String city) {
            this.name = name;
            this.city = city;
        }

        String name() { return name; }
        String city() { return city; }
        void setName(String name) { this.name = name; }

        @Override public boolean equals(Object o) {
            return o instanceof Member m && m.name.equals(name) && m.city.equals(city);
        }
        @Override public int hashCode() { return Objects.hash(name, city); }
        @Override public String toString() { return "Member[name=" + name + ", city=" + city + "]"; }
    }

    static class Registry {
        private final Map<Member, Integer> points = new HashMap<>();

        int size() { return points.size(); }
        void add(Member member, int pts) { points.put(member, pts); }
        Integer pointsOf(Member member) { return points.get(member); }

        // Gives the member a new name and keeps their points. Returns the renamed member, or null if unknown.
        Member rename(Member member, String newName) {
            if (!points.containsKey(member)) return null;
            member.setName(newName);               // changes the key while it's inside the map
            return member;
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
