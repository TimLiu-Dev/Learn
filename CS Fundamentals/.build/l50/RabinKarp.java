import java.nio.file.*;
import java.util.*;

// Rabin–Karp: a sliding window over a hash. Each slide updates the window's hash in O(1)
// instead of rehashing m characters. The hash is String.hashCode's polynomial,
// s[0]·31^(m-1) + … + s[m-1], in int arithmetic, so overflow wraps (mod 2^32).
public class RabinKarp {
    record Result(List<Integer> matches, int hashHits) {}

    static Result search(String text, String pat) {
        int m = pat.length();
        if (m == 0 || m > text.length()) return new Result(List.of(), 0);
        int high = 1;                                        // 31^(m-1): the weight of the char that leaves
        for (int i = 1; i < m; i++) high *= 31;
        int target = pat.hashCode();
        int h = 0;
        for (int i = 0; i < m; i++) h = 31 * h + text.charAt(i);   // hash of the first window (Horner's rule)
        List<Integer> matches = new ArrayList<>();
        int hits = 0;
        for (int i = 0; i <= text.length() - m; i++) {
            if (i > 0) h = 31 * (h - text.charAt(i - 1) * high) + text.charAt(i + m - 1);   // drop left, add right
            if (h == target) {
                hits++;
                if (text.regionMatches(i, pat, 0, m)) matches.add(i);                   // hashes can collide: verify
            }
        }
        return new Result(matches, hits);
    }

    // Every window's rolling hash, to compare with substring(...).hashCode().
    static int[] rollingHashes(String text, int m) {
        int high = 1;
        for (int i = 1; i < m; i++) high *= 31;
        int[] out = new int[text.length() - m + 1];
        int h = 0;
        for (int i = 0; i < m; i++) h = 31 * h + text.charAt(i);
        for (int i = 0; i < out.length; i++) {
            if (i > 0) h = 31 * (h - text.charAt(i - 1) * high) + text.charAt(i + m - 1);
            out[i] = h;
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        String book = Files.readString(Path.of("data/alice.txt"));
        int[] rolled = rollingHashes(book, 8);
        boolean same = true;
        for (int i = 0; i < rolled.length; i++) same &= rolled[i] == book.substring(i, i + 8).hashCode();
        System.out.println(rolled.length + " windows of length 8: rolling hash == substring hashCode? " + same);

        Result r = search(book, "Alice");
        int naive = 0;
        for (int from = book.indexOf("Alice"); from >= 0; from = book.indexOf("Alice", from + 1)) naive++;
        System.out.println("\"Alice\": " + r.matches().size() + " matches, " + r.hashHits() + " hash hits, indexOf finds " + naive);

        Result c = search("BBAaBB", "Aa");                   // "Aa" and "BB" share a hashCode: 2112
        System.out.println("\"Aa\" in \"BBAaBB\": matches at " + c.matches() + ", " + c.hashHits() + " hash hits");
    }
}
