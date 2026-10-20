import java.util.ArrayList;
import java.util.List;

// Counts valid e-mail addresses. Something in here is slow: let JFR find it.
public class Emails {
    static final String EMAIL = "[a-z0-9.]+@[a-z0-9]+\\.[a-z]{2,}";

    static boolean isValid(String s) {
        return s.matches(EMAIL);
    }

    public static void main(String[] args) {
        List<String> inbox = new ArrayList<>();
        for (int i = 0; i < 2_000_000; i++) {
            inbox.add(i % 7 == 0 ? "not an email " + i : "user" + i + "@example.com");
        }
        long start = System.nanoTime();
        int valid = 0;
        for (int round = 0; round < 3; round++) {
            for (String s : inbox) if (isValid(s)) valid++;
        }
        System.out.println(valid + " valid in " + (System.nanoTime() - start) / 1_000_000 + " ms");
    }
}
