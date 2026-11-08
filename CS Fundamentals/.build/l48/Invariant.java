import java.util.Arrays;

public class Invariant {
    // Invariant: at the top of every pass, best == the largest of a[0..i).
    static int largest(int[] a) {
        if (a.length == 0) throw new IllegalArgumentException("empty array");
        int best = a[0];
        int i = 1;
        while (i < a.length) {
            assert best == Arrays.stream(a, 0, i).max().getAsInt() : "invariant broken at i=" + i;
            if (a[i] > best) best = a[i];
            i++;
        }
        // The loop ended, so i == a.length: the invariant now says best is the largest of all of a.
        return best;
    }

    // Same loop with one bug: best starts at 0 instead of a[0].
    static int largestBuggy(int[] a) {
        int best = 0;
        int i = 1;
        while (i < a.length) {
            assert best == Arrays.stream(a, 0, i).max().getAsInt() : "invariant broken at i=" + i + ": best=" + best;
            if (a[i] > best) best = a[i];
            i++;
        }
        return best;
    }

    public static void main(String[] args) {
        int[] temps = {-5, -2, -9};
        System.out.println("largest = " + largest(temps));
        System.out.println("largestBuggy = " + largestBuggy(temps));
    }
}
