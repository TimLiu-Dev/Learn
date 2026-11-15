import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class Practice {
    // The first element that matches, or an empty Optional. Stop as soon as you find it.
    static <T> Optional<T> myFind(Iterable<? extends T> items, Predicate<? super T> p) {
        throw new UnsupportedOperationException("TODO: loop, and return as soon as p.test says yes");
    }

    // The leading elements while p holds
    static <T> List<T> myTakeWhile(Iterable<? extends T> items, Predicate<? super T> p) {
        throw new UnsupportedOperationException("TODO: collect items until the first miss");
    }

    // CHECKS
    public static void main(String[] args) throws Exception {
        check("myFind returns the first match", Optional.of("avocado"),
                () -> myFind(List.of("apple", "avocado", "banana", "artichoke"), w -> w.length() > 5));
        check("myFind gives empty when nothing matches", Optional.empty(), () -> myFind(List.of(1, 3, 5), n -> n % 2 == 0));
        check("myFind stops at the first match", 3, () -> {
            AtomicInteger calls = new AtomicInteger();
            myFind(List.of(4, 8, 15, 16, 23), n -> { calls.incrementAndGet(); return n > 10; });
            return calls.get();
        });
        check("a Predicate<Object> works on strings (? super T)", Optional.of("banana"), () -> {
            Predicate<Object> longText = o -> o.toString().length() > 5;
            return myFind(List.of("kiwi", "banana"), longText);
        });
        check("myTakeWhile stops at the first miss", List.of(1, 2), () -> myTakeWhile(List.of(1, 2, 5, 1), n -> n < 3));
        check("myTakeWhile can return nothing", List.of(), () -> myTakeWhile(List.of(9, 1), n -> n < 3));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
