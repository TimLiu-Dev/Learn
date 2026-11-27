import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Practice {
    // Each method must match a sequential loop and stay parallel.

    // The squares of nums, in the same order as nums.
    static List<Integer> squares(List<Integer> nums) {
        List<Integer> out = new ArrayList<>();
        nums.parallelStream().map(x -> x * x).forEach(out::add);
        return out;
    }

    // opening plus every deposit.
    static long balance(long opening, List<Long> deposits) {
        return deposits.parallelStream().reduce(opening, Long::sum);
    }

    // How many words have more than 5 letters.
    static int countLong(List<String> words) {
        int[] n = {0};                               // a local can't be changed from a lambda, so: an array
        words.parallelStream().forEach(w -> { if (w.length() > 5) n[0]++; });
        return n[0];
    }

    // CHECKS
    public static void main(String[] args) {
        List<Integer> nums = IntStream.rangeClosed(1, 200_000).map(i -> i % 1000).boxed().toList();
        List<Integer> expected = nums.stream().map(x -> x * x).toList();
        List<Long> deposits = LongStream.rangeClosed(1, 100_000).boxed().toList();
        List<String> words = IntStream.range(0, 400_000).mapToObj(i -> i % 3 == 0 ? "parallel" : "seq").toList();

        check("squares of [3, 1, 2]", List.of(9, 1, 4), () -> squares(List.of(3, 1, 2)));
        for (int run = 0; run < 5; run++)
            check("squares of 200,000 numbers keep their order, run " + run, true, () -> squares(nums).equals(expected));
        check("balance with no deposits", 100L, () -> balance(100L, List.of()));
        for (int run = 0; run < 5; run++)
            check("balance adds the opening amount once, run " + run, 100L + 5_000_050_000L, () -> balance(100L, deposits));
        check("countLong of a short list", 1, () -> countLong(List.of("thread", "lock", "pool")));
        for (int run = 0; run < 5; run++)
            check("countLong of 400,000 words, run " + run, 133_334, () -> countLong(words));
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
