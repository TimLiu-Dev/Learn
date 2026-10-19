import java.util.ArrayList;
import java.util.List;

public class Boxing {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>();   // List<int> is not allowed
        nums.add(7);                              // autoboxing: int -> Integer
        int first = nums.get(0);                  // unboxing: Integer -> int
        System.out.println(first);

        Integer maybe = null;                     // a wrapper CAN be null...
        int crash = maybe;                        // ...and unboxing it throws
    }
}
