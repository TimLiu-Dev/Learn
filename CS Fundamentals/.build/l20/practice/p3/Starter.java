import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;

public class Practice {
    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    static boolean isMinHeap(int[] a) {
        throw new UnsupportedOperationException("TODO: every index i > 0 must be no smaller than its parent (i - 1) / 2");
    }

    static void siftDown(int[] a, int i, int n) {
        throw new UnsupportedOperationException("TODO: swap down with the smaller child while bigger than it");
    }

    static void heapify(int[] a) {
        throw new UnsupportedOperationException("TODO: siftDown every parent, from the last parent back to the root");
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
