import java.util.*;

// "Remove duplicates, keep first-seen order": the kind of code an assistant writes in seconds.
public class Dedupe {
    static List<String> uniqueSlow(List<String> words) {
        List<String> result = new ArrayList<>();
        for (String w : words) {
            if (!result.contains(w)) result.add(w);   // contains on a List scans it: O(n) per check
        }
        return result;                                // n checks * O(n) each = O(n^2)
    }

    static List<String> uniqueFast(List<String> words) {
        Set<String> seen = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (String w : words) {
            if (seen.add(w)) result.add(w);           // HashSet add/contains: O(1) on average
        }
        return result;                                // n * O(1) = O(n)
    }

    public static void main(String[] args) {
        List<String> words = List.of("to", "be", "or", "not", "to", "be");
        System.out.println(uniqueSlow(words));
        System.out.println(uniqueFast(words));
        System.out.println(new ArrayList<>(new LinkedHashSet<>(words)));  // the JDK idiom, O(n)
    }
}
