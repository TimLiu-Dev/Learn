import java.util.*;

public class Practice {
    // The longest substring with at most k distinct characters; the earliest one wins a tie.
    static String longestAtMostK(String s, int k) {
        Map<Character, Integer> counts = new HashMap<>();   // char -> how many times it's inside the window
        int left = 0, bestStart = 0, bestLen = 0;
        for (int right = 0; right < s.length(); right++) {
            throw new UnsupportedOperationException("TODO: add the char, shrink while more than k distinct, update the best");
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    // CHECKS
    public static void main(String[] args) {
        check("eceba with k = 2 gives ece", "ece", () -> longestAtMostK("eceba", 2));
        check("k = 1 finds the longest run of one letter", "bbb", () -> longestAtMostK("abbbcc", 1));
        check("a tie goes to the window that starts first", "aabb", () -> longestAtMostK("aabbcc", 2));
        check("the window shrinks until a letter really leaves", "accc", () -> longestAtMostK("abaccc", 2));
        check("a large enough k takes the whole string", "abcabc", () -> longestAtMostK("abcabc", 10));
        check("k = 0 gives an empty answer", "", () -> longestAtMostK("abc", 0));
        check("the empty string gives an empty answer", "", () -> longestAtMostK("", 2));
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
