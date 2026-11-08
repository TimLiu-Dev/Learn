import java.io.File;
import java.util.Objects;
import java.util.Properties;

public class Practice {
    static int port(Properties config) {
        String raw = config.getProperty("port");   // may be null, and the type doesn't say so
        if (raw == null) return 8080;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 8080;
        }
    }

    static String folderOf(String path) {
        String parent = new File(path).getParent();
        return parent != null ? parent : ".";
    }

    static String describe(String path) {
        File file = new File(path);
        return file.getName() + (file.isAbsolute() ? " (absolute)" : " (relative)");
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
