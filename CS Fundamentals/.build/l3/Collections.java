import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Collections {
    public static void main(String[] args) {
        List<String> fruits = List.of("apple", "pear", "apple");
        Set<String> unique = new LinkedHashSet<>(fruits);
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("apple", 3);
        stock.put("pear", 0);
        System.out.println(fruits);
        System.out.println(unique);
        System.out.println(stock);
        System.out.println(fruits.get(0));
        System.out.println(stock.get("apple"));
        System.out.println(stock.get("kiwi"));
        System.out.println(stock.getOrDefault("kiwi", 0));

        List<String> todo = new ArrayList<>(List.of("write", "test"));
        todo.add("commit");
        todo.remove("test");
        todo.set(0, "WRITE");
        System.out.println(todo);
        System.out.println(todo.contains("commit"));
        System.out.println(todo.size());

        for (Map.Entry<String, Integer> e : stock.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String f : fruits) counts.put(f, counts.getOrDefault(f, 0) + 1);
        System.out.println(counts);

        List<Integer> nums = new ArrayList<>(List.of(3, 1, 2));
        java.util.Collections.sort(nums);
        System.out.println(nums);
        int[] arr = {3, 1, 2};
        System.out.println(arr.length);
        System.out.println(Arrays.toString(arr));

        fruits.add("kiwi");   // List.of is unmodifiable
    }
}
