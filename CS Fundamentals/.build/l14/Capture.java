import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Capture {
    public static void main(String[] args) {
        int[] box = {0};                                   // workaround 1: a one-slot array
        List.of(3, 4, 5).forEach(n -> box[0] += n);
        System.out.println(box[0]);

        AtomicInteger total = new AtomicInteger();         // workaround 2: a mutable object
        List.of(3, 4, 5).forEach(total::addAndGet);
        System.out.println(total.get());

        int sum = List.of(3, 4, 5).stream().mapToInt(n -> n).sum();  // better: no mutation
        System.out.println(sum);
    }
}
