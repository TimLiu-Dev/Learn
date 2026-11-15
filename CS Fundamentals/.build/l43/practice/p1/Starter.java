import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // Each replica stamps its writes with its OWN counter, so two replicas can produce the same ts.
    record Versioned(String value, long ts, String node) { }

    // The winner of two versions of one key. Every replica must pick the same one,
    // whichever order the two arrive in.
    static Versioned lww(Versioned a, Versioned b) {
        throw new UnsupportedOperationException("TODO: newer ts wins; on a tie, the larger node name wins");
    }

    static class Replica {
        final String name;
        private long counter = 0;
        final Map<String, Versioned> data = new HashMap<>();

        Replica(String name) { this.name = name; }

        Versioned write(String key, String value) {
            Versioned v = new Versioned(value, ++counter, name);
            data.put(key, v);
            return v;
        }

        // A version from the peer arrives: maybe late, maybe twice.
        void receive(String key, Versioned incoming) {
            throw new UnsupportedOperationException("TODO: keep lww(mine, incoming), or incoming if there's nothing yet");
        }
    }

    // CHECKS
    public static void main(String[] args) {
        Versioned old = new Versioned("[book]", 1, "A"), newer = new Versioned("[book,pen]", 2, "A");
        check("the newer timestamp wins", newer, () -> lww(old, newer));
        check("the newer timestamp wins in the other order too", newer, () -> lww(newer, old));
        Versioned ta = new Versioned("[book,lamp]", 1, "A"), tb = new Versioned("[book,pen]", 1, "B");
        check("a tie goes to the larger node name", tb, () -> lww(ta, tb));
        check("a tie goes to the larger node name in the other order too", tb, () -> lww(tb, ta));
        check("two replicas agree after a partition heals", true, () -> {
            Replica a = new Replica("A"), b = new Replica("B");
            Versioned fromA = a.write("cart", "[book,lamp]"), fromB = b.write("cart", "[book,pen]");
            a.receive("cart", fromB);
            b.receive("cart", fromA);
            return a.data.get("cart").equals(b.data.get("cart"));
        });
        check("a duplicate or a late old version changes nothing", "[book,lamp]", () -> {
            Replica a = new Replica("A"), b = new Replica("B");
            Versioned v1 = a.write("cart", "[book]"), v2 = a.write("cart", "[book,lamp]");
            b.receive("cart", v2);
            b.receive("cart", v2);
            b.receive("cart", v1);
            return b.data.get("cart").value();
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
