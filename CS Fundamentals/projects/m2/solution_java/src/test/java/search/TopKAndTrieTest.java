package search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class TopKAndTrieTest {

    @Test
    void topKKeepsTheBestInOrder() {
        assertEquals(List.of(9, 8, 7), TopK.select(List.of(3, 9, 1, 7, 8, 2), 3, Comparator.<Integer>reverseOrder()));
        assertEquals(List.of(2, 1), TopK.select(List.of(1, 2), 5, Comparator.<Integer>reverseOrder()));
        assertThrows(IllegalArgumentException.class, () -> TopK.select(List.of(1), 0, Comparator.<Integer>naturalOrder()));
    }

    /** Property: the heap agrees with "sort everything, take k" on random inputs with many ties. */
    @Test
    void topKMatchesAFullSort() {
        Random random = new Random(7);
        for (int run = 0; run < 1_000; run++) {
            List<Trie.Entry> items = new ArrayList<>();
            int n = random.nextInt(40);
            for (int i = 0; i < n; i++) items.add(new Trie.Entry("w" + i, 1 + random.nextInt(4)));
            int k = 1 + random.nextInt(8);
            List<Trie.Entry> expected = items.stream().sorted(Index.BY_COUNT).limit(k).toList();
            assertEquals(expected, TopK.select(items, k, Index.BY_COUNT));
        }
    }

    @Test
    void prefixIncludesTheWordItselfButNotInnerNodes() {
        Trie trie = new Trie();
        trie.add("car", 2);
        trie.add("card", 5);
        trie.add("cart", 2);
        List<String> found = trie.withPrefix("car").stream().sorted(Index.BY_COUNT).map(Trie.Entry::term).toList();
        assertEquals(List.of("card", "car", "cart"), found);
        assertEquals(3, trie.withPrefix("ca").size(), "'ca' is a path, not a word");
        assertEquals(List.of(), trie.withPrefix("cars"));
    }

    /** Property: the trie finds exactly what a brute-force filter over all words finds. */
    @Test
    void trieMatchesBruteForce() {
        Random random = new Random(11);
        for (int run = 0; run < 300; run++) {
            Trie trie = new Trie();
            Map<String, Long> words = new HashMap<>();
            for (int i = 0; i < 30; i++) {
                String w = randomWord(random);
                long c = 1 + random.nextInt(3);
                trie.add(w, c);
                words.merge(w, c, Long::sum);
            }
            String prefix = randomWord(random); // 1 to 4 letters
            List<Trie.Entry> expected = words.entrySet().stream().filter(e -> e.getKey().startsWith(prefix))
                    .map(e -> new Trie.Entry(e.getKey(), e.getValue())).sorted(Index.BY_COUNT).toList();
            assertEquals(expected, trie.withPrefix(prefix).stream().sorted(Index.BY_COUNT).toList());
        }
    }

    private static String randomWord(Random random) {
        StringBuilder w = new StringBuilder();
        int n = 1 + random.nextInt(4);
        for (int i = 0; i < n; i++) w.append((char) ('a' + random.nextInt(3)));
        return w.toString();
    }
}
