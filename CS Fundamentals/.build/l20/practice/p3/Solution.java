import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;

public class Practice {
    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    // Checking each child against its parent covers every parent-child pair once.
    static boolean isMinHeap(int[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i] < a[(i - 1) / 2]) return false;
        return true;
    }

    static void siftDown(int[] a, int i, int n) {
        int p = i;
        while (true) {
            int l = 2 * p + 1;
            if (l >= n) return;                                 // no children: done
            int c = (l + 1 < n && a[l + 1] < a[l]) ? l + 1 : l;   // the SMALLER child
            if (a[p] <= a[c]) return;                           // already no bigger than both
            swap(a, p, c);
            p = c;
        }
    }

    // Bottom-up: when we reach a parent, both of its subtrees are already heaps.
    static void heapify(int[] a) {
        for (int i = a.length / 2 - 1; i >= 0; i--) siftDown(a, i, a.length);
    }

    // CHECKS
    static int[] heapified(int... a) { heapify(a); return a; }

    static List<Integer> list(int[] a) { List<Integer> out = new ArrayList<>(); for (int x : a) out.add(x); return out; }

    public static void main(String[] args) {
        check("isMinHeap accepts PriorityQueue's array", true, () -> isMinHeap(new int[] {1, 2, 8, 5, 3, 9}));
        check("isMinHeap accepts an empty array", true, () -> isMinHeap(new int[] {}));
        check("isMinHeap rejects a child smaller than its parent", false, () -> isMinHeap(new int[] {1, 5, 2, 4}));
        check("siftDown swaps with the smaller child", "[2, 4, 3, 6, 5, 9, 8]", () -> { int[] a = {9, 4, 2, 6, 5, 3, 8}; siftDown(a, 0, a.length); return Arrays.toString(a); });
        check("heapify builds the lesson's input bottom-up", "[1, 2, 8, 3, 5, 9]", () -> Arrays.toString(heapified(5, 1, 8, 3, 2, 9)));
        check("heapify fixes reverse-sorted input", true, () -> isMinHeap(heapified(9, 8, 7, 6, 5, 4, 3, 2, 1)));
        check("heapify matches PriorityQueue built from a collection", true, () -> {
            Random rnd = new Random(20);
            for (int t = 0; t < 200; t++) {
                int[] a = new int[rnd.nextInt(16)];
                for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
                List<Integer> expected = new ArrayList<>(new PriorityQueue<>(list(a)));
                if (!expected.equals(list(heapified(a)))) return false;
            }
            return true;
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
