import java.util.*;

public class Practice {
    record ShelfConfig(String db, int port, int poolSize) {}

    /** Spring's relaxed binding for environment variables: "shelf.pool-size" -> "SHELF_POOLSIZE". */
    static String envName(String property) {
        throw new UnsupportedOperationException("TODO: dots to underscores, remove dashes, uppercase");
    }

    /** Reads each setting from env (under its relaxed name), else props, else the default. */
    static ShelfConfig bind(Map<String, String> props, Map<String, String> env) {
        throw new UnsupportedOperationException("TODO: look up, convert and range-check each setting; report every problem at once");
    }
    // CHECKS
    static String failure(Check call) {
        try {
            call.get();
            return "no exception";
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        check("env names follow relaxed binding", "SHELF_POOLSIZE SPRING_MAIN_LOGSTARTUPINFO",
            () -> envName("shelf.pool-size") + " " + envName("spring.main.log-startup-info"));
        check("defaults apply when nothing is set", new ShelfConfig("shelf.db", 8080, 4), () -> bind(Map.of(), Map.of()));
        check("properties are read and converted", new ShelfConfig("data.db", 9000, 8),
            () -> bind(Map.of("shelf.db", "data.db", "shelf.port", "9000", "shelf.pool-size", "8"), Map.of()));
        check("an environment variable beats the properties file", new ShelfConfig("shelf.db", 7000, 2),
            () -> bind(Map.of("shelf.port", "9000"), Map.of("SHELF_PORT", "7000", "SHELF_POOLSIZE", "2", "PATH", "/usr/bin")));
        check("port 0 is allowed, for a random port in tests", 0, () -> bind(Map.of("shelf.port", "0"), Map.of()).port());
        check("every problem is reported at once",
            "IllegalArgumentException: shelf.port: 'abc' is not a number; shelf.pool-size: 0 is out of range 1..64",
            () -> failure(() -> bind(Map.of("shelf.port", "abc", "shelf.pool-size", "0"), Map.of())));
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
