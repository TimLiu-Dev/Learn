import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Practice {
    /** The next SemVer release after current, given the commit messages since it, or null if there's nothing to release. */
    static String nextVersion(String current, List<String> commits) {
        throw new UnsupportedOperationException("TODO: parse current, find the biggest change in commits, bump that number");
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
