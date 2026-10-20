import java.util.List;
import java.util.Set;
import java.util.function.*;

public class Refs {
    static boolean isEven(int n) { return n % 2 == 0; }

    record Badge(String name) {}

    public static void main(String[] args) {
        List<Integer> nums = List.of(1, 2, 3, 4);
        List<String> words = List.of("kotlin", "java", "jvm");

        // 1. static method: Type::method
        System.out.println(nums.stream().filter(Refs::isEven).toList());
        // 2. unbound: the receiver becomes the first parameter
        ToIntFunction<String> len = String::length;
        System.out.println(words.stream().mapToInt(len).boxed().toList());
        // 3. bound: the receiver (this set) is fixed now
        Set<String> jvmLangs = Set.of("kotlin", "java", "scala");
        Predicate<String> onJvm = jvmLangs::contains;
        System.out.println(words.stream().filter(onJvm).toList());
        // 4. constructor: Type::new
        Function<String, Badge> make = Badge::new;
        System.out.println(words.stream().map(make).map(b -> b.name().toUpperCase()).toList());
    }
}
