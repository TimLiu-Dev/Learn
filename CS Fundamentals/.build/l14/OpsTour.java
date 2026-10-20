import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

public class OpsTour {
    public static void main(String[] args) {
        List<List<Integer>> weeks = List.of(List.of(30, 45), List.of(20), List.of(60, 15, 25));
        List<Integer> nums = List.of(3, 1, 4, 1, 5, 9, 2, 6);

        System.out.println(weeks.stream().flatMap(List::stream).toList());
        List<String> letters = List.of("a", "b", "c");                 // no zip: use indexes
        System.out.println(IntStream.range(0, 3).mapToObj(i -> letters.get(i) + "=" + (i + 1)).toList());
        System.out.println(nums.stream().reduce(0, (acc, n) -> acc + n));      // like fold
        System.out.println(nums.stream().reduce(Math::max));                   // Optional!
        System.out.println(Stream.of("7", "x", "42")
            .filter(s -> s.matches("-?\\d+")).map(Integer::parseInt).toList());
        System.out.println(nums.stream().distinct().toList());
        System.out.println(nums.stream().limit(3).toList() + " " + nums.stream().skip(5).toList());
        System.out.println(nums.stream().gather(Gatherers.windowFixed(3)).toList());   // JDK 24+
        System.out.println(nums.stream().gather(Gatherers.windowSliding(3))
            .map(w -> w.stream().mapToInt(Integer::intValue).sum()).toList());
        System.out.println(Stream.of("run", "yoga").collect(Collectors.toMap(Function.identity(), String::length)));
        System.out.println(Stream.<Integer>empty().reduce(Integer::sum));      // no exception

        List<Integer> result = nums.stream().toList();
        result.add(7);                                                          // throws
    }
}
