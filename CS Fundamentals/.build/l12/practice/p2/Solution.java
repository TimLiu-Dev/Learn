import java.util.*;

public class Practice {
    // "grace hopper" -> "GH": one upper-case letter per word.
    static String initials(String name) {
        String[] parts = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;   // stray spaces make empty "words"
            result.append(Character.toUpperCase(part.charAt(0)));
        }
        return result.toString();
    }

    static List<String> badges(List<String> roster) {
        List<String> out = new ArrayList<>();
        for (String name : roster) out.add(initials(name));
        return out;
    }

    // CHECKS
    static final List<String> ROSTER = List.of(
        "Ada Lovelace", "Alan Turing", "Barbara Liskov", "Edsger Dijkstra",
        "Frances Allen", "Grace  Hopper", "Donald Knuth", "John McCarthy",
        "Katherine Johnson", " Linus Torvalds", "Margaret Hamilton", "Tim Berners Lee");

    public static void main(String[] args) {
        check("two words give two initials", "AL", () -> initials("Ada Lovelace"));
        check("lower-case words are capitalised", "GH", () -> initials("grace hopper"));
        check("one word gives one initial", "C", () -> initials("Cher"));
        check("every name on the roster gets a badge",
            List.of("AL", "AT", "BL", "ED", "FA", "GH", "DK", "JM", "KJ", "LT", "MH", "TBL"), () -> badges(ROSTER));
        check("a blank name gives an empty badge", "|", () -> initials("") + "|" + initials("   "));
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
