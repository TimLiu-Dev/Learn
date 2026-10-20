import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class CompareCounts {
    static int[] randomArray(int n) {
        Random rnd = new Random(42);                   // fixed seed: same data every run
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt(1_000_000);
        return a;
    }

    static int[] sortedCopy(int[] a) {
        int[] s = a.clone();
        Arrays.sort(s);
        return s;
    }

    static long count(int[] input, Consumer<int[]> sort) {
        int[] a = input.clone();
        Less.compares = 0;
        sort.accept(a);
        if (!Arrays.equals(a, sortedCopy(input))) throw new IllegalStateException("not sorted!");
        return Less.compares;
    }

    public static void main(String[] args) {
        String[] names = {"insertion", "merge", "quick"};
        List<Consumer<int[]>> sorts = List.of(InsertionSort::sort, MergeSort::sort, QuickSort::sort);
        for (int k = 0; k < 3; k++) {
            int[] a = {5, 2, 4, 6, 1, 3};
            sorts.get(k).accept(a);
            System.out.printf("%-9s %s%n", names[k], Arrays.toString(a));
        }
        System.out.println();
        System.out.println("     n | insertion: random  sorted | merge: random  sorted | quick: random     sorted");
        for (int n : new int[] {1_000, 2_000, 4_000}) {
            int[] random = randomArray(n);
            int[] sorted = sortedCopy(random);
            System.out.printf("%,6d | %,17d %,7d | %,13d %,7d | %,13d %,10d%n", n,
                count(random, InsertionSort::sort), count(sorted, InsertionSort::sort),
                count(random, MergeSort::sort), count(sorted, MergeSort::sort),
                count(random, QuickSort::sort), count(sorted, QuickSort::sort));
        }
        try {
            count(sortedCopy(randomArray(100_000)), QuickSort::sort);
        } catch (StackOverflowError e) {
            System.out.println("quick on 100,000 sorted items: StackOverflowError");
        }
    }
}
