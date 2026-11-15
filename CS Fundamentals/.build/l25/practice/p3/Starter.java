import java.util.*;

public class Practice {
    /** Compares "MAJOR.MINOR.PATCH" numerically: negative if a < b, 0 if equal, positive if a > b. */
    static int compareVersions(String a, String b) {
        throw new UnsupportedOperationException("TODO: split both on '.', then compare the numbers part by part");
    }

    /** Gradle's default conflict resolution: for each "group:name", the highest requested version wins. */
    static Map<String, String> resolve(List<String> requests) {
        throw new UnsupportedOperationException("TODO: group the coordinates by module, then keep the highest version of each");
    }

    /** SemVer: may upgrading from -> to break the code that calls this library? */
    static boolean isBreaking(String from, String to) {
        throw new UnsupportedOperationException("TODO: compare the MAJOR parts, and remember what SemVer says about 0.y.z");
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
