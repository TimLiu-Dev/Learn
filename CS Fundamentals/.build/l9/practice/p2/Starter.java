import java.io.File;
import java.util.Objects;
import java.util.Properties;

public class Practice {
    static int port(Properties config) {
        return Integer.parseInt(config.getProperty("port"));   // Java won't warn that this can be null
    }

    static String folderOf(String path) {
        throw new UnsupportedOperationException("TODO: new File(path).getParent(), or \".\" if it has none");
    }

    static String describe(String path) {
        throw new UnsupportedOperationException("TODO: \"NAME (absolute)\" or \"NAME (relative)\"");
    }

    // CHECKS
    static Properties config(String... keysAndValues) {
        Properties p = new Properties();
        for (int i = 0; i < keysAndValues.length; i += 2) p.setProperty(keysAndValues[i], keysAndValues[i + 1]);
        return p;
    }

    public static void main(String[] args) {
        check("port is read from the config", 9090, () -> port(config("port", "9090")));
        check("a missing port falls back to 8080", 8080, () -> port(config("host", "example.org")));
        check("a port that is not a number falls back to 8080", 8080, () -> port(config("port", "eighty")));
        check("folderOf gives the parent folder", "/var/log", () -> folderOf("/var/log/app.log"));
        check("a bare file name has no parent, so folderOf gives a dot", ".", () -> folderOf("notes.txt"));
        check("describe an absolute path", "app.log (absolute)", () -> describe("/var/log/app.log"));
        check("describe a relative path", "notes.txt (relative)", () -> describe("notes.txt"));
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
