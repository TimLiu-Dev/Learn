import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Practice {
    // Lines like `env` prints: KEY=value
    static Map<String, String> parseEnv(List<String> lines) {
        Map<String, String> env = new LinkedHashMap<>();
        for (String line : lines) {
            if (line.isBlank() || line.startsWith("#") || !line.contains("=")) continue;
            String[] kv = line.split("=", 2);   // limit 2: split at the first '=' only
            env.put(kv[0], kv[1]);              // a later line overwrites
        }
        return env;
    }

    // which: the first PATH directory that holds cmd wins; null if none
    static String which(String cmd, Map<String, String> env, Set<String> files) {
        String path = env.get("PATH");
        if (path == null) return null;
        for (String dir : path.split(":")) {
            String candidate = dir + "/" + cmd;
            if (files.contains(candidate)) return candidate;
        }
        return null;
    }

    // CHECKS
    public static void main(String[] args) {
        check("a value may itself contain equals signs", Map.of("JAVA_OPTS", "-Dmode=fast -Dlevel=2"), () -> parseEnv(List.of("JAVA_OPTS=-Dmode=fast -Dlevel=2")));
        check("blank lines, comments and lines without equals are skipped", Map.of("HOME", "/Users/you", "EMPTY", ""),
              () -> parseEnv(List.of("# settings", "", "HOME=/Users/you", "not a variable", "EMPTY=")));
        check("a later line wins", "debug", () -> parseEnv(List.of("LEVEL=info", "LEVEL=debug")).get("LEVEL"));
        Set<String> files = Set.of("/usr/bin/java", "/Users/you/fakebin/java", "/bin/ls");
        check("the first match in PATH wins", "/Users/you/fakebin/java", () -> which("java", Map.of("PATH", "/Users/you/fakebin:/usr/bin:/bin"), files));
        check("no match gives null", null, () -> which("kotlinc", Map.of("PATH", "/usr/bin:/bin"), files));
        check("no PATH gives null", null, () -> which("java", Map.of(), files));
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
