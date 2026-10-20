import java.util.List;

public class Wildcards {
    static void producer(List<? extends Number> nums) {
        Number first = nums.get(0);              // reading: fine
        nums.add(1);                             // writing: which list is this, really?
    }

    static void consumer(List<? super Integer> out) {
        out.add(1);                              // writing Integers: fine
        Integer first = out.get(0);              // reading: could be any supertype
    }
}
