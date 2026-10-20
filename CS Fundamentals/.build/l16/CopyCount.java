import java.util.function.IntUnaryOperator;

// Simulate n adds and count how many elements get copied during resizes.
public class CopyCount {
    static long copiesFor(int n, IntUnaryOperator grow) {
        int capacity = 4, size = 0;
        long copies = 0;
        for (int i = 0; i < n; i++) {
            if (size == capacity) {           // full: allocate a bigger array, copy everything over
                copies += size;
                capacity = grow.applyAsInt(capacity);
            }
            size++;
        }
        return copies;
    }

    static String cell(int n, IntUnaryOperator grow) {
        long c = copiesFor(n, grow);
        return String.format("%,d (%.1f)", c, (double) c / n);
    }

    public static void main(String[] args) {
        System.out.printf("%9s %22s %22s %22s%n", "n adds", "x2 copies (per add)", "x1.5 copies (per add)", "+10 copies (per add)");
        for (int n : new int[] {1_000, 10_000, 100_000, 1_000_000}) {
            System.out.printf("%,9d %22s %22s %22s%n", n,
                cell(n, c -> c * 2), cell(n, c -> c + c / 2), cell(n, c -> c + 10));
        }
    }
}
