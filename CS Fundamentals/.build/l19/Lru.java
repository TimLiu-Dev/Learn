import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Lru {
    public static void main(String[] args) {
        // accessOrder = true: get() moves an entry to the end (most recently used)
        Map<String, Integer> byAccess = new LinkedHashMap<>(16, 0.75f, true);
        for (String w : List.of("a", "b", "c")) byAccess.put(w, 0);
        byAccess.get("a");
        System.out.println("access order: " + byAccess.keySet());

        // Override removeEldestEntry: put() asks it after every insert
        Map<String, Integer> cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > 3;
            }
        };
        for (String w : List.of("a", "b", "c")) cache.put(w, (int) w.charAt(0));
        cache.get("a");                                  // touch a: b is now least recent
        cache.put("d", 100);                             // 4 entries > 3: evict the eldest
        System.out.println("cache: " + cache.keySet());
    }
}
