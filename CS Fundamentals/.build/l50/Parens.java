import java.util.ArrayList;
import java.util.List;

// Every balanced string of n pairs of brackets, pruned as it is built:
// an opener is allowed while fewer than n are used; a closer only if it has an opener to match.
public class Parens {
    static List<String> balanced(int n) {
        List<String> out = new ArrayList<>();
        go(n, 0, 0, new StringBuilder(), out);
        return out;
    }

    private static void go(int n, int open, int close, StringBuilder sb, List<String> out) {
        if (sb.length() == 2 * n) { out.add(sb.toString()); return; }
        if (open < n) { sb.append('('); go(n, open + 1, close, sb, out); sb.setLength(sb.length() - 1); }
        if (close < open) { sb.append(')'); go(n, open, close + 1, sb, out); sb.setLength(sb.length() - 1); }
    }

    public static void main(String[] args) {
        System.out.println(balanced(3));
        List<Integer> counts = new ArrayList<>();
        for (int n = 1; n <= 10; n++) counts.add(balanced(n).size());
        System.out.println(counts);                     // OEIS A000108: the Catalan numbers
    }
}
