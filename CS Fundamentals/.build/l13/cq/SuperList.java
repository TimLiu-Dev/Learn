import java.util.ArrayList;
import java.util.List;

public class SuperList {
    static void addOnes(List<? super Integer> out) {
        out.add(1);
        out.add(1);
    }

    public static void main(String[] args) {
        List<Number> nums = new ArrayList<>(List.of(2.5));
        addOnes(nums);
        System.out.println(nums);
    }
}
