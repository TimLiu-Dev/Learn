import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WordFreq {
    public static void main(String[] args) {
        String text = """
            The cat sat on the mat. The mat was flat,
            and the cat was fat! A cat, a mat, a hat.""";

        Map<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (String word : text.toLowerCase().split("[^a-z]+")) {
            if (word.isEmpty()) continue;
            counts.put(word, counts.getOrDefault(word, 0) + 1);
            total++;
        }

        String best = "";
        int bestCount = 0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            String word = e.getKey();
            int count = e.getValue();
            if (count > bestCount || (count == bestCount && word.compareTo(best) < 0)) {
                best = word;
                bestCount = count;
            }
        }

        System.out.println("total words: " + total);
        System.out.println("unique words: " + counts.size());
        System.out.println("most frequent: " + best + " (" + bestCount + ")");
        System.out.print("repeated:");
        for (Map.Entry<String, Integer> e : new TreeMap<>(counts).entrySet()) {
            if (e.getValue() >= 2) System.out.print(" " + e.getKey() + "=" + e.getValue());
        }
        System.out.println();
    }
}
