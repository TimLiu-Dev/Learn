import java.util.ArrayList;
import java.util.List;

public class Ex3 {
    // Merge two sorted lists into one sorted list. Ties take from `a` first (stable).
    static <T extends Comparable<? super T>> List<T> merge(List<T> a, List<T> b) {
        List<T> out = new ArrayList<>(a.size() + b.size());
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            if (b.get(j).compareTo(a.get(i)) < 0) out.add(b.get(j++)); else out.add(a.get(i++));
        }
        while (i < a.size()) out.add(a.get(i++));
        while (j < b.size()) out.add(b.get(j++));
        return out;
    }

    static int shifts = 0;

    static void insertionSortCounting(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && key < a[j]) { a[j + 1] = a[j]; j--; shifts++; }
            a[j + 1] = key;
        }
    }

    public static void main(String[] args) {
        System.out.println(merge(List.of(1, 4, 9), List.of(2, 3, 10, 11)));
        System.out.println(merge(List.of("b", "d"), List.of()));

        int[] nearly = new int[1_000];
        for (int i = 0; i < nearly.length; i++) nearly[i] = i;
        nearly[10] = 500; nearly[500] = 10;                    // one swap away from sorted
        insertionSortCounting(nearly);
        System.out.println("nearly sorted: " + shifts + " shifts");
        shifts = 0;
        int[] reversed = new int[1_000];
        for (int i = 0; i < reversed.length; i++) reversed[i] = 999 - i;
        insertionSortCounting(reversed);
        System.out.println("reversed: " + shifts + " shifts");
    }
}
