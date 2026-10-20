import java.util.function.*;

public class Basics {
    // No function types: a lambda must target a functional interface
    static Function<Integer, Integer> doubler = x -> x * 2;
    static UnaryOperator<Integer> square = x -> x * x;  // Function<T,T>
    static Runnable greet = () -> System.out.println("hello");
    static Consumer<String> onDone = null;              // any reference can be null

    static int applyTwice(Function<Integer, Integer> f, int x) {
        return f.apply(f.apply(x));                     // must name the method
    }

    static boolean retry(int times, IntPredicate block) {  // int -> boolean, no boxing
        for (int attempt = 1; attempt <= times; attempt++) {
            if (block.test(attempt)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(doubler.apply(5));
        System.out.println(applyTwice(doubler, 3));
        System.out.println(applyTwice(x -> x + 10, 1));
        greet.run();
        if (onDone != null) onDone.accept("ignored");
        onDone = msg -> System.out.println("done: " + msg);
        if (onDone != null) onDone.accept("saved");

        boolean ok = retry(3, attempt -> {              // lambda stays inside ( )
            System.out.println("attempt " + attempt);
            return attempt == 2;
        });
        System.out.println("succeeded=" + ok);
    }
}
