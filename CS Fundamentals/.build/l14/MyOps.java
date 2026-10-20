import java.util.*;
import java.util.function.*;

public class MyOps {
    // ? super T: f may accept T or any supertype. ? extends R: it may return any subtype of R
    static <T, R> List<R> myMap(Iterable<? extends T> items, Function<? super T, ? extends R> f) {
        List<R> result = new ArrayList<>();
        for (T item : items) result.add(f.apply(item));
        return result;
    }

    static <T> List<T> myFilter(Iterable<? extends T> items, Predicate<? super T> keep) {
        List<T> result = new ArrayList<>();
        for (T item : items) if (keep.test(item)) result.add(item);
        return result;
    }

    static <T, A> A myFold(Iterable<? extends T> items, A initial,
                           BiFunction<? super A, ? super T, ? extends A> combine) {
        A acc = initial;
        for (T item : items) acc = combine.apply(acc, item);
        return acc;
    }

    // map and filter as special cases of fold (the accumulator is a list we add to)
    static <T, R> List<R> mapViaFold(Iterable<? extends T> items, Function<? super T, ? extends R> f) {
        return myFold(items, new ArrayList<R>(), (acc, item) -> { acc.add(f.apply(item)); return acc; });
    }

    static <T> List<T> filterViaFold(Iterable<? extends T> items, Predicate<? super T> keep) {
        return myFold(items, new ArrayList<T>(), (acc, item) -> { if (keep.test(item)) acc.add(item); return acc; });
    }

    public static void main(String[] args) {
        List<String> words = List.of("fold", "map", "filter", "zip");
        System.out.println(myMap(words, String::length).equals(words.stream().map(String::length).toList()));
        System.out.println(myFilter(words, w -> w.length() > 3));
        System.out.println(myFold(words, 0, (total, w) -> total + w.length()));
        System.out.println(mapViaFold(words, String::toUpperCase));
        System.out.println(filterViaFold(words, w -> w.contains("i")));

        // Why the wildcards matter: a general-purpose function written for Object...
        Function<Object, String> describe = o -> "<" + o + ">";
        List<CharSequence> tags = myMap(words, describe);   // ...works on Strings, gives CharSequences
        System.out.println(tags);
    }
}
