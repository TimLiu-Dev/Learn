import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Practice {
    static final Pattern HEADER = Pattern.compile("^(\\w+)(\\([^)]*\\))?(!)?: ");   // type, optional (scope), optional !

    /** The next SemVer release after current, given the commit messages since it, or null if there's nothing to release. */
    static String nextVersion(String current, List<String> commits) {
        if (!current.matches("\\d+\\.\\d+\\.\\d+")) throw new IllegalArgumentException("not a MAJOR.MINOR.PATCH version: " + current);
        String[] parts = current.split("\\.");
        int major = Integer.parseInt(parts[0]), minor = Integer.parseInt(parts[1]), patch = Integer.parseInt(parts[2]);

        boolean breaking = false, feat = false, fix = false;
        for (String message : commits) {
            List<String> lines = message.lines().toList();
            Matcher m = HEADER.matcher(lines.get(0));
            boolean matched = m.find();
            if (matched && m.group(3) != null) breaking = true;
            for (String line : lines.subList(1, lines.size())) {
                if (line.startsWith("BREAKING CHANGE:") || line.startsWith("BREAKING-CHANGE:")) breaking = true;
            }
            if (matched && m.group(1).equals("feat")) feat = true;
            if (matched && m.group(1).equals("fix")) fix = true;
        }
        if (breaking && major == 0) return "0." + (minor + 1) + ".0";   // 0.x: anything may change, so MINOR acts as MAJOR
        if (breaking) return (major + 1) + ".0.0";
        if (feat) return major + "." + (minor + 1) + ".0";
        if (fix) return major + "." + minor + "." + (patch + 1);
        return null;
    }

    // CHECKS
    public static void main(String[] args) {
        check("a fix bumps the patch", "1.4.3", () -> nextVersion("1.4.2", List.of("fix: trim spaces around the name", "docs: fix a typo")));
        check("a feature bumps the minor and resets the patch", "1.5.0", () -> nextVersion("1.4.2", List.of("fix: trim spaces", "feat(greet): add farewell")));
        check("a bang bumps the major", "2.0.0", () -> nextVersion("1.4.2", List.of("feat(api)!: drop the /v1 routes")));
        check("a BREAKING CHANGE footer bumps the major", "2.0.0", () -> nextVersion("1.4.2",
                List.of("fix: x", "refactor: rename Book.title\n\nBREAKING CHANGE: clients must read `name` instead of `title`")));
        check("no feat or fix means nothing to release", null, () -> nextVersion("1.4.2", List.of("docs: explain how to run", "chore: bump Gradle")));
        check("while major is 0 a breaking change bumps the minor", "0.5.0", () -> nextVersion("0.4.2", List.of("feat!: new config format")));
        check("a tag name is not a version", "IllegalArgumentException", () -> {
            try {
                return nextVersion("v1.2.3", List.of("fix: x"));
            } catch (IllegalArgumentException e) {
                return "IllegalArgumentException";
            }
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
