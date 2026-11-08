package katas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AutocompleteTest {
    private static Autocomplete of(Object... wordThenCount) {
        Autocomplete ac = new Autocomplete();
        for (int i = 0; i < wordThenCount.length; i += 2) ac.add((String) wordThenCount[i], (Integer) wordThenCount[i + 1]);
        return ac;
    }

    private static List<String> words(List<Suggestion> s) { return s.stream().map(Suggestion::word).toList(); }

    @Test void emptyAutocompleteSuggestsNothing() {
        assertTrue(new Autocomplete().suggest("a", 3).isEmpty());
    }

    @Test void unknownPrefixGivesAnEmptyList() {
        assertTrue(of("cat", 1).suggest("dog", 3).isEmpty());
    }

    @Test void ranksByCountMostFrequentFirst() {
        assertEquals(List.of("ten", "to", "tea"), words(of("tea", 2, "ten", 9, "to", 5).suggest("t", 5)));
    }

    @Test void tiesAreBrokenAlphabetically() {
        assertEquals(List.of("bad", "ball", "bat"), words(of("bat", 3, "ball", 3, "bad", 3).suggest("ba", 5)));
    }

    @Test void kLimitsTheNumberOfSuggestions() {
        Autocomplete ac = of("a", 1, "ab", 2, "abc", 3, "abd", 4);
        assertEquals(List.of(new Suggestion("abd", 4), new Suggestion("abc", 3)), ac.suggest("a", 2));
    }

    @Test void aPrefixThatIsItselfAWordIsIncluded() {
        assertEquals(List.of("cart", "car"), words(of("car", 1, "cart", 2).suggest("car", 5)));
    }

    @Test void addTextCountsWordsCaseInsensitively() {
        Autocomplete ac = new Autocomplete();
        ac.addText("The cat. THE hat, the mat!");
        assertEquals(List.of(new Suggestion("the", 3)), ac.suggest("th", 1));
        assertEquals(4, ac.size());
    }

    @Test void emptyPrefixRanksEveryWord() {
        assertEquals(List.of("y", "z", "x"), words(of("x", 1, "y", 3, "z", 2).suggest("", 10)));
    }

    @Test void kMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> of("a", 1).suggest("a", 0));
    }

    @Test void matchesABruteForceScanOnRandomDictionaries() {
        Random rnd = new Random(50);
        for (int t = 0; t < 500; t++) {
            Map<String, Integer> counts = new HashMap<>();
            Autocomplete ac = new Autocomplete();
            int words = rnd.nextInt(1, 40);
            for (int i = 0; i < words; i++) {
                StringBuilder w = new StringBuilder();
                int len = rnd.nextInt(1, 5);
                for (int j = 0; j < len; j++) w.append("abc".charAt(rnd.nextInt(3)));   // tiny alphabet: many shared prefixes
                int n = rnd.nextInt(1, 4);
                ac.add(w.toString(), n);
                counts.merge(w.toString(), n, Integer::sum);
            }
            StringBuilder p = new StringBuilder();
            int plen = rnd.nextInt(0, 3);
            for (int j = 0; j < plen; j++) p.append("abc".charAt(rnd.nextInt(3)));
            int k = rnd.nextInt(1, 6);
            List<Suggestion> want = counts.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(p.toString()))
                    .map(e -> new Suggestion(e.getKey(), e.getValue()))
                    .sorted(Comparator.comparingInt(Suggestion::count).reversed().thenComparing(Suggestion::word))
                    .limit(k).toList();
            assertEquals(want, ac.suggest(p.toString(), k), "prefix '" + p + "', k=" + k + ", counts=" + counts);
        }
    }
}
