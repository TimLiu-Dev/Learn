package katas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Random;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class TinyRegexTest {
    private static boolean m(String pattern, String input) { return new TinyRegex(pattern).matches(input); }

    @Test void aLiteralMustMatchTheWholeInput() {
        assertTrue(m("abc", "abc"));
        assertFalse(m("abc", "ab"));
        assertFalse(m("abc", "abcd"));
    }

    @Test void dotMatchesExactlyOneCharacter() {
        assertTrue(m("a.c", "abc"));
        assertTrue(m("a.c", "a.c"));
        assertFalse(m("a.c", "ac"));
    }

    @Test void starPlusAndQuestionMark() {
        assertTrue(m("ab*", "a")); assertTrue(m("ab*", "abbb"));
        assertFalse(m("ab+", "a")); assertTrue(m("ab+", "abb"));
        assertTrue(m("ab?c", "ac")); assertTrue(m("ab?c", "abc")); assertFalse(m("ab?c", "abbc"));
    }

    @Test void alternationHasTheLowestPrecedenceAndParenthesesGroup() {
        assertTrue(m("ab|cd", "ab")); assertTrue(m("ab|cd", "cd")); assertFalse(m("ab|cd", "abd"));
        assertTrue(m("a(b|c)d", "acd")); assertFalse(m("a(b|c)d", "ab"));
        assertTrue(m("(ab)*", "ababab")); assertFalse(m("(ab)*", "aba"));
    }

    @Test void emptyPatternAndEmptyBranchesMatchTheEmptyString() {
        assertTrue(m("", "")); assertFalse(m("", "a"));
        assertTrue(m("a|", "")); assertTrue(m("a|", "a"));
        assertTrue(m("()", ""));
    }

    @Test void aBackslashMakesAMetacharacterLiteral() {
        assertTrue(m("a\\.b", "a.b"));
        assertFalse(m("a\\.b", "axb"));
        assertTrue(m("\\(\\*\\)", "(*)"));
    }

    @Test void malformedPatternsAreRejected() {
        for (String bad : new String[] {"(ab", "ab)", "*a", "a**", "a*?", "a\\", "[ab]", "a{2}"})
            assertThrows(IllegalArgumentException.class, () -> new TinyRegex(bad), bad);
    }

    private final Random rnd = new Random(51);

    private String gen(int depth) {
        return switch (depth == 0 ? rnd.nextInt(3) : rnd.nextInt(7)) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> ".";
            case 3 -> gen(depth - 1) + gen(depth - 1);
            case 4 -> gen(depth - 1) + "|" + gen(depth - 1);
            case 5 -> "(" + gen(depth - 1) + ")" + "*+?".charAt(rnd.nextInt(3));
            default -> "(" + gen(depth - 1) + ")";
        };
    }

    @Test void agreesWithJavaUtilRegexOnThousandsOfRandomCases() {
        int cases = 0;
        for (int i = 0; i < 2_000; i++) {
            String pattern = gen(4);
            TinyRegex ours = new TinyRegex(pattern);
            Pattern theirs = Pattern.compile(pattern);
            for (int j = 0; j < 25; j++) {
                StringBuilder input = new StringBuilder();
                for (int k = rnd.nextInt(9); k > 0; k--) input.append("ab".charAt(rnd.nextInt(2)));
                assertEquals(theirs.matcher(input).matches(), ours.matches(input),
                    "pattern " + pattern + ", input \"" + input + "\"");
                cases++;
            }
        }
        assertEquals(50_000, cases);
    }

    @Test void patternsThatDefeatBacktrackingStayFast() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            assertFalse(m("(a+)+b", "a".repeat(100_000) + "c"));
            assertTrue(m("a?".repeat(30) + "a".repeat(30), "a".repeat(30)));
            assertFalse(m("(.*a)".repeat(12), "a".repeat(28) + "x"));
        });
    }

    @Test void deeplyNestedStarsDoNotOverflowTheStack() {
        assertTrue(m("((a*)*)*", "a".repeat(50_000)));
    }
}
