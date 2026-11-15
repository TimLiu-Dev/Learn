import java.util.*;

public class Touch {
    public static void main(String[] args) {
        Map<String, Integer> cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > 2;
            }
        };
        cache.put("a", 1);
        cache.put("b", 2);
        if (cache.containsKey("a")) System.out.print("a is cached; ");
        cache.put("c", 3);
        System.out.println(cache.keySet());
    }
}
