package search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class TokenizerTest {

    @ParameterizedTest(name = "\"{0}\" → [{1}]")
    @CsvSource(delimiter = '|', quoteCharacter = '"', textBlock = """
            Fox FOX fox                 | fox fox fox
            well-known e-mail           | well known e mail
            don't 'tis dogs'            | don't tis dogs
            rock'n'roll x''y            | rock'n'roll x y
            Æsop’s Fables               | æsop's fables
            naïve café ÉCOLE            | naïve café école
            C3PO 3.14 x_y               | c3po 3 14 x y
            The cat and THE hat         | cat hat
            """)
    void tokenizesBySpec(String text, String expected) {
        assertEquals(List.of(expected.split(" ")), Tokenizer.tokenize(text));
    }

    @Test
    void onlySeparatorsAndStopWordsGiveNoTokens() {
        assertEquals(List.of(), Tokenizer.tokenize(" -- ' '' ... The AND of "));
    }

    @Test
    void lettersOutsideTheBmpAreOneCodePoint() {
        // U+1D400 MATHEMATICAL BOLD CAPITAL A is a letter, stored as two chars.
        assertEquals(List.of("𝐀b"), Tokenizer.tokenize("𝐀B!"));
    }

    @Test
    void stopWordsAreFiftyLowerCaseWords() {
        assertEquals(50, Tokenizer.STOP_WORDS.size());
        for (String w : Tokenizer.STOP_WORDS) assertEquals(w.toLowerCase(Locale.ROOT), w);
    }

    /** Property: on random text, every token is lower-case, well-formed, and re-tokenises to itself. */
    @Test
    void randomTextGivesWellFormedTokens() {
        String alphabet = "abcAB'’ -é1.\n";
        Random random = new Random(42);
        for (int run = 0; run < 2_000; run++) {
            StringBuilder text = new StringBuilder();
            int n = random.nextInt(30);
            for (int i = 0; i < n; i++) text.append(alphabet.charAt(random.nextInt(alphabet.length())));
            List<String> tokens = Tokenizer.tokenize(text.toString());
            for (String t : tokens) {
                assertTrue(t.matches("[\\p{L}\\p{N}]+('[\\p{L}\\p{N}]+)*"), () -> "bad token '" + t + "' from: " + text);
                assertEquals(t.toLowerCase(Locale.ROOT), t, () -> "not lower-case, from: " + text);
                assertTrue(!Tokenizer.STOP_WORDS.contains(t), () -> "stop word kept, from: " + text);
            }
            assertEquals(tokens, Tokenizer.tokenize(String.join(" ", tokens)), () -> "not idempotent: " + text);
        }
    }
}
