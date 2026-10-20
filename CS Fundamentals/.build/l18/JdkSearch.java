import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JdkSearch {
    static String describe(int r) {
        return r >= 0 ? "found at " + r : "missing, insert at " + (-r - 1);   // decode -(ip) - 1
    }

    public static void main(String[] args) {
        int[] a = {10, 20, 30, 40, 50};
        for (int key : new int[] {30, 35, 5, 99}) {
            int r = Arrays.binarySearch(a, key);
            System.out.println(key + " -> " + r + "  (" + describe(r) + ")");
        }
        List<String> words = List.of("apple", "fig", "kiwi", "pear");
        System.out.println("kiwi -> " + Collections.binarySearch(words, "kiwi")
                + ", grape -> " + Collections.binarySearch(words, "grape"));
        int[] unsorted = {50, 10, 40, 20, 30};
        System.out.println("unsorted, 10 -> " + Arrays.binarySearch(unsorted, 10));   // garbage in, garbage out
    }
}
