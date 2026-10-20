import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ex4 {
    static Collection<List<String>> anagramGroups(List<String> words) {
        Map<String, List<String>> groups = new LinkedHashMap<>();   // first-seen order
        for (String w : words) {
            char[] cs = w.toCharArray();
            Arrays.sort(cs);
            groups.computeIfAbsent(new String(cs), k -> new ArrayList<>()).add(w);
        }
        return groups.values();
    }

    static Character firstUnique(String s) {
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : s.toCharArray()) counts.merge(c, 1, Integer::sum);
        for (Map.Entry<Character, Integer> e : counts.entrySet()) if (e.getValue() == 1) return e.getKey();
        return null;
    }

    public static void main(String[] args) {
        System.out.println(anagramGroups(List.of("listen", "google", "enlist", "banana", "inlets", "gogole", "silent")));
        System.out.println(firstUnique("swiss") + " " + firstUnique("level") + " " + firstUnique("aabb"));
    }
}
