import java.util.ArrayList;
import java.util.List;

public class Remove {
    public static void main(String[] args) {
        List<Integer> nums = new ArrayList<>(List.of(10, 20, 30));
        nums.remove(1);
        System.out.println(nums);
    }
}
