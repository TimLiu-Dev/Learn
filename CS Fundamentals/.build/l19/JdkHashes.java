import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JdkHashes {
    public static void main(String[] args) {
        List<String> words = List.of("pear", "apple", "fig", "kiwi", "banana", "cherry");

        Map<String, Integer> hash = new HashMap<>();
        Map<String, Integer> linked = new LinkedHashMap<>();
        Map<String, Integer> tree = new TreeMap<>();
        for (String w : words) { hash.put(w, w.length()); linked.put(w, w.length()); tree.put(w, w.length()); }
        System.out.println("HashMap:       " + hash.keySet());
        System.out.println("LinkedHashMap: " + linked.keySet());
        System.out.println("TreeMap:       " + tree.keySet());

        for (int i = 1; i <= 20; i++) hash.put("extra" + i, i);   // grows past 12 entries: resize to 32
        for (int i = 1; i <= 20; i++) hash.remove("extra" + i);
        System.out.println("HashMap again: " + hash.keySet());   // same keys, different order
    }
}
