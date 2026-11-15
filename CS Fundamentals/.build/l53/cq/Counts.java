import java.util.*;

public class Counts {
    public static void main(String[] args) {
        // three counts from uniq -c, as text
        List<String> counts = new ArrayList<>(List.of("99868", "301266", "5"));
        Collections.sort(counts);
        System.out.println(counts);
    }
}
