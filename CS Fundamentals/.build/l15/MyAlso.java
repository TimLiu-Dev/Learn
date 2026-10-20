import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MyAlso {
    static <T> T also(T obj, Consumer<T> block) {
        block.accept(obj);
        return obj;
    }

    static <T> T takeIf(T obj, Predicate<T> predicate) { return predicate.test(obj) ? obj : null; }

    static <T, R> R run(T obj, Function<T, R> block) { return block.apply(obj); }

    public static void main(String[] args) {
        int n = also(41, x -> System.out.println("saw " + x)) + 1;
        System.out.println(n);
        System.out.println(takeIf("abc", s -> s.length() == 3));
        System.out.println(takeIf("abcd", s -> s.length() == 3));
        String shout = run("kotlin", s -> s.toUpperCase() + s.length());
        System.out.println(shout);
    }
}
