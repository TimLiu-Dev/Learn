package search;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class IndexTest {
    // 4 documents; "fox" is in 3 of them, "grapes" and "sour" in 2, "milk" in 1.
    private final Index index = Index.of(Map.of(
            "a", "The fox and the grapes. Fox!",
            "b", "A sour fox.",
            "c", "Sour grapes? Sour milk.",
            "d", "fox"));
    private final Commands commands = new Commands(index);

    @Test
    void idfIsSmoothedNaturalLog() {
        assertEquals(Math.log(5.0 / 4.0) + 1, index.idf("fox"), 1e-12);
        assertEquals(Math.log(5.0 / 1.0) + 1, index.idf("unknown"), 1e-12);
    }

    @Test
    void andRanksByTfIdfThenName() {
        // idf(fox) = ln(5/4) + 1 = 1.2231, so a scores 2 × 1.2231; b and d tie and go by name.
        assertEquals("""
                results: 3
                1. a 2.4463
                2. b 1.2231
                3. d 1.2231""", commands.run("q fox"));
        assertEquals("""
                results: 1
                1. a 3.9571""", commands.run("q FOX grapes fox"));
        assertEquals("results: 0", commands.run("q fox milk"));
    }

    @Test
    void orScoresEveryMatchingTerm() {
        assertEquals("""
                results: 3
                1. c 4.5325
                2. a 1.5108
                3. b 1.5108""", commands.run("or sour grapes"));
    }

    @Test
    void statsDocPrefixAndTop() {
        assertEquals("""
                documents: 4
                tokens: 10
                terms: 4
                average document length: 2.50""", commands.run("stats"));
        assertEquals("""
                doc: c
                tokens: 4
                terms: 3
                top terms: sour (2), grapes (1), milk (1)""", commands.run("doc c"));
        assertEquals("1. fox 4", commands.run("prefix F"));
        assertEquals("""
                1. fox 4
                2. sour 3""", commands.run("top 2"));
        assertEquals("no suggestions", commands.run("prefix z"));
    }

    @Test
    void errorsAreOneLine() {
        assertEquals("error: no document named 'zzz'", commands.run("doc zzz"));
        assertEquals("error: no searchable terms in query", commands.run("q the and of"));
        assertEquals("error: usage: top <k> (k is a positive integer)", commands.run("top 0"));
        assertEquals("error: unknown command 'find'", commands.run("find fox"));
    }

    /** Property: AND equals a brute-force scan of every document, on random corpora. */
    @Test
    void andAgreesWithAScan() {
        Random random = new Random(3);
        String[] vocab = {"ant", "bee", "cat", "dog", "eel"};
        for (int run = 0; run < 200; run++) {
            Map<String, String> texts = new java.util.HashMap<>();
            for (int d = 0; d < 8; d++) {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < 6; i++) text.append(vocab[random.nextInt(vocab.length)]).append(' ');
                texts.put("d" + d, text.toString());
            }
            Index idx = Index.of(texts);
            List<String> query = List.of(vocab[random.nextInt(5)], vocab[random.nextInt(5)]).stream().distinct().toList();
            var expected = texts.entrySet().stream()
                    .filter(e -> Tokenizer.tokenize(e.getValue()).containsAll(query))
                    .map(Map.Entry::getKey).collect(Collectors.toSet());
            var actual = idx.and(query).stream().map(Index.Hit::name).collect(Collectors.toSet());
            assertEquals(expected, actual, () -> "query " + query + " over " + texts);
        }
    }
}
