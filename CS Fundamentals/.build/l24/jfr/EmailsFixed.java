import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Same program, with the pattern compiled once and reused.
public class EmailsFixed {
    static final Pattern EMAIL = Pattern.compile("[a-z0-9.]+@[a-z0-9]+\\.[a-z]{2,}"); // once

    static boolean isValid(String s) {
        return EMAIL.matcher(s).matches();
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
