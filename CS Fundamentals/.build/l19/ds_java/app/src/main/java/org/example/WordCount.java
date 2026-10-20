package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/** L3's word-frequency report, counted with MyHashMap instead of the JDK's map. */
public final class WordCount {
    private WordCount() {}

    public static String report(String text) {
        MyHashMap<String, Integer> counts = new MyHashMap<>();
        int total = 0;
        for (String raw : text.split("[^\\p{L}]+")) {
            if (raw.isEmpty()) continue;
            String word = raw.toLowerCase();
            Integer old = counts.get(word);
            counts.put(word, old == null ? 1 : old + 1);
            total++;
        }

        List<String> words = new ArrayList<>(counts.keys());
        Collections.sort(words);
        String best = words.get(0);
        for (String w : words) if (counts.get(w) > counts.get(best)) best = w;   // sorted, so ties keep the first
        StringJoiner repeated = new StringJoiner(" ");
        for (String w : words) if (counts.get(w) >= 2) repeated.add(w + "=" + counts.get(w));

        return "total words: " + total + "\n"
                + "unique words: " + counts.size() + "\n"
                + "most frequent: " + best + " (" + counts.get(best) + ")\n"
                + "repeated: " + repeated;
    }
}
