import java.util.Map;
import java.util.Objects;

public class Practice {
    // Integer, not int, so it can return null.
    static Integer parseAge(String text) {
        if (text == null) return null;
        int n;
        try {
            n = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
        return (n >= 0 && n <= 150) ? n : null;
    }

    static String bossOfBoss(String name, Map<String, String> bosses) {
        String boss = bosses.get(name);
        if (boss == null) return null;        // the compiler wouldn't have reminded you
        return bosses.get(boss);
    }

    // CHECKS
    public static void main(String[] args) {
        Map<String, String> bosses = Map.of("ada", "grace", "grace", "linus");
        check("42 is an age", 42, () -> parseAge("42"));
        check("0 is an age", 0, () -> parseAge("0"));
        check("150 is an age", 150, () -> parseAge("150"));
        check("null text gives null", null, () -> parseAge(null));
        check("not a number gives null", null, () -> parseAge("abc"));
        check("empty text gives null", null, () -> parseAge(""));
        check("-1 gives null", null, () -> parseAge("-1"));
        check("151 gives null", null, () -> parseAge("151"));
        check("the boss of the boss is two steps up", "linus", () -> bossOfBoss("ada", bosses));
        check("a missing second step gives null", null, () -> bossOfBoss("grace", bosses));
        check("a missing first step gives null", null, () -> bossOfBoss("alan", bosses));
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
