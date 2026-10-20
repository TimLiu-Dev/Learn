import java.util.function.IntUnaryOperator;

public class Compose {
    static IntUnaryOperator compose(IntUnaryOperator f, IntUnaryOperator g) {
        return x -> g.applyAsInt(f.applyAsInt(x));
    }

    static IntUnaryOperator repeated(int n, IntUnaryOperator f) {
        return x -> {
            int result = x;
            for (int i = 0; i < n; i++) result = f.applyAsInt(result);
            return result;
        };
    }

    static int addOne(int x) { return x + 1; }

    public static void main(String[] args) {
        IntUnaryOperator twice = x -> x * 2;
        IntUnaryOperator doubleThenAdd = compose(twice, Compose::addOne);
        IntUnaryOperator addThenDouble = compose(Compose::addOne, twice);
        System.out.println(doubleThenAdd.applyAsInt(5));
        System.out.println(addThenDouble.applyAsInt(5));
        System.out.println(repeated(3, twice).applyAsInt(1));
        System.out.println(repeated(0, twice).applyAsInt(7));
        // The JDK already has this one: f.andThen(g)
        System.out.println(twice.andThen(Compose::addOne).applyAsInt(5));
    }
}
