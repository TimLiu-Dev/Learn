import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// My first Branches run built its "random" array with a fresh generator per element.
public class Seeds {
    public static void main(String[] args) {
        List<Integer> firsts = new ArrayList<>();
        for (int s = 42; s < 50; s++) firsts.add(new Random(s).nextInt(256));
        System.out.println("new Random(42..49).nextInt(256): " + firsts);
        StringBuilder pattern = new StringBuilder();
        for (int s = 42; s < 106; s++) pattern.append(new Random(s).nextInt(256) >= 128 ? '1' : '0');
        System.out.println("is it >= 128, for 64 seeds:      " + pattern);
        Random rng = new Random(42);
        StringBuilder good = new StringBuilder();
        for (int i = 0; i < 64; i++) good.append(rng.nextInt(256) >= 128 ? '1' : '0');
        System.out.println("one new Random(42), 64 draws:    " + good);
    }
}
