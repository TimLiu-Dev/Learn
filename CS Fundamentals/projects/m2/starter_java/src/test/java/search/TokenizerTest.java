package search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.junit.jupiter.api.Test;

/** Examples of the testing style. Add your own for every rule in the spec. */
class TokenizerTest {

    @Test
    void stopWordsAreFiftyLowerCaseWords() {
        assertEquals(50, Tokenizer.STOP_WORDS.size());
        for (String w : Tokenizer.STOP_WORDS) assertEquals(w.toLowerCase(Locale.ROOT), w);
    }

    @Test
    void lowerCasesSplitsAndDropsStopWords() {
        assertEquals(List.of("fox", "jumped", "over", "lazy", "dog"), Tokenizer.tokenize("The Fox jumped over-the LAZY dog!"));
    }

    /** A property test: whatever the text, no token is empty, upper-case or a stop word. */
    @Test
    void randomTextGivesCleanTokens() {
        String alphabet = "abcAB -.!\n";
        Random random = new Random(42); // seeded, so a failure always reproduces
        for (int run = 0; run < 1_000; run++) {
            StringBuilder text = new StringBuilder();
            for (int i = 0, n = random.nextInt(30); i < n; i++) text.append(alphabet.charAt(random.nextInt(alphabet.length())));
            for (String t : Tokenizer.tokenize(text.toString())) {
                assertTrue(!t.isEmpty() && t.equals(t.toLowerCase(Locale.ROOT)) && !Tokenizer.STOP_WORDS.contains(t),
                        () -> "bad token '" + t + "' from: " + text);
            }
        }
    }
}
