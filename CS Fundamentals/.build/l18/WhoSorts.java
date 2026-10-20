import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WhoSorts {
    // Make the comparator throw, then read the stack trace to see who was calling it.
    static final Comparator<String> SPY = (a, b) -> {
        if (a.equals("kiwi") || b.equals("kiwi")) throw new IllegalStateException("who is sorting?");
        return a.compareTo(b);
    };

    public static void main(String[] args) {
        List<String> xs = new ArrayList<>(List.of("pear", "fig", "kiwi", "apple"));
        try {
            xs.sort(SPY);
        } catch (IllegalStateException e) {
            for (StackTraceElement f : List.of(e.getStackTrace()).subList(1, 6)) System.out.println("  at " + f);
        }
    }
}
