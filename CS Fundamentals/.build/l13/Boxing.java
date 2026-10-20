import java.util.List;

public class Boxing {
    public static void main(String[] args) {
        List<Integer> big = List.of(1000, 1000); // List<int> is illegal, so Integer
        System.out.println(big.get(0).getClass().getName());
        System.out.println(big.get(0) == big.get(1));   // compares references (L5)!
    }
}
