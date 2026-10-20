import java.util.*;
import java.util.function.Function;

public class StrictMap {
    // No wildcards: f must be EXACTLY Function<T, R>
    static <T, R> List<R> strictMap(List<T> items, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T item : items) result.add(f.apply(item));
        return result;
    }

    public static void main(String[] args) {
        List<String> words = List.of("fold", "map");
        Function<Object, String> describe = o -> "<" + o + ">";
        List<CharSequence> tags = strictMap(words, describe);
    }
}
