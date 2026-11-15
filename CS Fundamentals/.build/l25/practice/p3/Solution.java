import java.util.*;

public class Practice {
    static int[] parts(String version) {
        return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    /** Compares "MAJOR.MINOR.PATCH" numerically: negative if a < b, 0 if equal, positive if a > b. */
    static int compareVersions(String a, String b) {
        int[] x = parts(a), y = parts(b);
        for (int i = 0; i < 3; i++) {
            if (x[i] != y[i]) return Integer.compare(x[i], y[i]);   // first difference decides
        }
        return 0;
    }

    /** Gradle's default conflict resolution: for each "group:name", the highest requested version wins. */
    static Map<String, String> resolve(List<String> requests) {
        Map<String, String> chosen = new TreeMap<>();
        for (String coordinate : requests) {
            int colon = coordinate.lastIndexOf(':');
            String module = coordinate.substring(0, colon), version = coordinate.substring(colon + 1);
            chosen.merge(module, version, (old, now) -> compareVersions(now, old) > 0 ? now : old);
        }
        return chosen;
    }

    /** SemVer: may upgrading from -> to break the code that calls this library? */
    static boolean isBreaking(String from, String to) {
        int[] old = parts(from), now = parts(to);
        if (old[0] == 0) return !from.equals(to);                    // 0.y.z: anything may change
        return now[0] != old[0];
    }

    // CHECKS
    public static void main(String[] args) {
        check("versions compare numerically, not as text", "+ - 0",
                () -> sign(compareVersions("1.10.0", "1.9.0")) + " " + sign(compareVersions("2.0.0", "10.0.0")) + " " + sign(compareVersions("3.1.4", "3.1.4")));
        check("the highest requested version wins", Map.of("org.junit.jupiter:junit-jupiter-engine", "6.0.1"),
                () -> resolve(List.of("org.junit.jupiter:junit-jupiter-engine:5.10.1", "org.junit.jupiter:junit-jupiter-engine:6.0.1")));
        check("each module is resolved on its own", Map.of("com.example:json", "1.10.0", "org.slf4j:slf4j-api", "2.0.17"),
                () -> resolve(List.of("com.example:json:1.9.0", "org.slf4j:slf4j-api:2.0.17", "com.example:json:1.10.0", "com.example:json:1.2.3")));
        check("only a major bump is breaking", List.of(true, false, false),
                () -> List.of(isBreaking("2.4.1", "3.0.0"), isBreaking("2.4.1", "2.5.0"), isBreaking("2.4.1", "2.4.2")));
        check("before version 1 anything may break", List.of(true, true),
                () -> List.of(isBreaking("0.3.1", "0.4.0"), isBreaking("0.3.1", "0.3.2")));
    }

    static String sign(int n) { return n > 0 ? "+" : n < 0 ? "-" : "0"; }

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
