import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Once {
    public static void main(String[] args) {
        List<Integer> log = new ArrayList<>();
        Stream<Integer> s = Stream.of(1, 2, 3).map(n -> { log.add(n); return n * 10; });
        System.out.print(log + " ");
        System.out.print(s.filter(n -> n > 10).findFirst().get() + " ");
        System.out.print(log + " ");
        try {
            System.out.println(s.count());
        } catch (IllegalStateException e) {
            System.out.println("reused");
        }
    }
}
