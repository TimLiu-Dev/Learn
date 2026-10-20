import java.util.ArrayList;
import java.util.List;

public class Variance {
    // PRODUCER: we only read Numbers out, so accept List<Integer>, List<Double>, ...
    static double sum(List<? extends Number> nums) {
        double total = 0;
        for (Number n : nums) total += n.doubleValue();
        return total;
    }

    // CONSUMER: we only put Integers in, so accept List<Integer>, List<Number>, List<Object>
    static void addSquares(List<? super Integer> out, int n) {
        for (int i = 1; i <= n; i++) out.add(i * i);
    }

    // both at once: the same shape as java.util.Collections.copy
    static <T> void copyInto(List<? extends T> src, List<? super T> dst) {
        for (T item : src) dst.add(item);
    }

    // unknown element type: we can still read Objects
    static int countNonNull(List<?> items) {
        int c = 0;
        for (Object o : items) if (o != null) c++;
        return c;
    }

    // arrays are covariant, so String[] already fits Object[] (and writes are unchecked)
    static String describeAll(Object[] items) {
        StringBuilder sb = new StringBuilder();
        for (Object o : items) { if (sb.length() > 0) sb.append('|'); sb.append(o); }
        return sb.toString();
    }

    public static void main(String[] args) {
        List<Integer> ints = List.of(1, 2, 3);
        List<Double> doubles = List.of(0.5, 0.25);
        System.out.println(sum(ints) + " " + sum(doubles));

        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        addSquares(numbers, 3);
        addSquares(objects, 2);
        System.out.println(numbers + " " + objects);

        copyInto(doubles, numbers);                       // T = Number
        System.out.println(numbers);
        System.out.println(countNonNull(List.of("a", "b")) + " " + countNonNull(ints));
        System.out.println(describeAll(new String[] {"x", "y"}));
    }
}
