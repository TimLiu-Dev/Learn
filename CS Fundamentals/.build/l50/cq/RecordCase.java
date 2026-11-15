import java.util.Arrays;

public class RecordCase {
    record Case(int[] a, int target) {}

    public static void main(String[] args) {
        Case x = new Case(new int[] {1, 4}, 4);
        Case y = new Case(new int[] {1, 4}, 4);
        System.out.println(x.equals(y) + " " + Arrays.equals(x.a(), y.a()));
    }
}
