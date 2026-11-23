import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Practice {
    // The storage service checks fencing tokens: a write whose token has gone
    // backwards comes from a lock holder whose lease has already expired.
    static class FencedStore {
        // Accept the write (and return true) unless a higher token was already accepted for this key.
        boolean write(String key, String value, long token) {
            throw new UnsupportedOperationException("TODO: compare with the highest token seen for this key");
        }

        String read(String key) { throw new UnsupportedOperationException("TODO: the key's current value"); }
    }

    // CHECKS
    public static void main(String[] args) {
        check("the paused holder's late write is rejected", "true true false v2", () -> {
            FencedStore s = new FencedStore();
            boolean a = s.write("report", "v1", 33), b = s.write("report", "v2", 34), c = s.write("report", "v1-late", 33);
            return a + " " + b + " " + c + " " + s.read("report");
        });
        check("the current holder can write several times with one token", "true true final", () -> {
            FencedStore s = new FencedStore();
            boolean a = s.write("report", "draft", 34), b = s.write("report", "final", 34);
            return a + " " + b + " " + s.read("report");
        });
        check("a rejected write changes nothing, even when it is retried", "false false v2", () -> {
            FencedStore s = new FencedStore();
            s.write("report", "v2", 34);
            boolean a = s.write("report", "stale", 33), b = s.write("report", "stale", 33);
            return a + " " + b + " " + s.read("report");
        });
        check("a newer holder still gets through after a rejection", "true v3", () -> {
            FencedStore s = new FencedStore();
            s.write("report", "v2", 34); s.write("report", "stale", 33);
            return s.write("report", "v3", 35) + " " + s.read("report");
        });
        check("each key is fenced on its own", "true true run 7 null", () -> {
            FencedStore s = new FencedStore();
            boolean a = s.write("invoices", "run 50", 50), b = s.write("emails", "run 7", 7);
            return a + " " + b + " " + s.read("emails") + " " + s.read("payroll");
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
