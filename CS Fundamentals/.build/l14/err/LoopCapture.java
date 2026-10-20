import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class LoopCapture {
    public static void main(String[] args) {
        List<IntSupplier> actions = new ArrayList<>();
        for (int j = 1; j <= 3; j++) {
            actions.add(() -> j);      // j changes every iteration
        }
    }
}
