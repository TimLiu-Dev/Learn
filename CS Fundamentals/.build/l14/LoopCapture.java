import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class LoopCapture {
    public static void main(String[] args) {
        List<IntSupplier> fromFor = new ArrayList<>();
        for (int i : new int[] {1, 2, 3}) {
            fromFor.add(() -> i);      // enhanced for: a fresh i each time
        }
        System.out.println(fromFor.stream().map(s -> s.getAsInt()).toList());

        List<IntSupplier> copied = new ArrayList<>();
        for (int j = 1; j <= 3; j++) {
            int copy = j;              // copy into an effectively final local
            copied.add(() -> copy);
        }
        System.out.println(copied.stream().map(s -> s.getAsInt()).toList());
    }
}
