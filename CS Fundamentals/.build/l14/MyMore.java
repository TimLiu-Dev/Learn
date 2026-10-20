import java.util.*;
import java.util.function.*;

public class MyMore {
    static <T, K> Map<K, List<T>> myGroupBy(Iterable<? extends T> items, Function<? super T, ? extends K> key) {
        Map<K, List<T>> groups = new LinkedHashMap<>();
        for (T item : items) groups.computeIfAbsent(key.apply(item), k -> new ArrayList<>()).add(item);
        return groups;
    }

    static <T, C extends Comparable<? super C>> Optional<T> myMaxBy(
            Iterable<? extends T> items, Function<? super T, ? extends C> selector) {
        T best = null;
        C bestKey = null;
        for (T item : items) {
            C k = selector.apply(item);
            if (bestKey == null || k.compareTo(bestKey) > 0) { best = item; bestKey = k; }
        }
        return Optional.ofNullable(best);
    }

    static <T> int myCount(Iterable<? extends T> items, Predicate<? super T> predicate) {
        int n = 0;
        for (T item : items) if (predicate.test(item)) n++;
        return n;
    }

    public static void main(String[] args) {
        List<String> words = List.of("apple", "avocado", "banana", "blueberry", "cherry");
        System.out.println(myGroupBy(words, w -> w.charAt(0)));
        System.out.println(myMaxBy(words, String::length));
        System.out.println(myMaxBy(List.<String>of(), String::length));
        System.out.println(myCount(words, w -> w.length() > 5));
    }
}
