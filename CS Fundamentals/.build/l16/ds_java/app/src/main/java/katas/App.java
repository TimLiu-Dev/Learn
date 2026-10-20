package katas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class App {
    // A deliberately bad dynamic array: grows by a constant 10 slots instead of doubling.
    static class PlusTenList<T> {
        private Object[] data = new Object[4];
        private int size = 0;
        int size() { return size; }
        void add(T element) {
            if (size == data.length) data = Arrays.copyOf(data, data.length + 10);
            data[size++] = element;
        }
    }

    static void bestOf3Ms(String label, int n, IntUnaryOperator addAll) {
        addAll.applyAsInt(n / 10);                             // warm-up
        double best = Double.MAX_VALUE;
        int size = 0;
        for (int r = 0; r < 3; r++) {
            long t0 = System.nanoTime();
            size = addAll.applyAsInt(n);                       // keep the result: no dead code
            best = Math.min(best, (System.nanoTime() - t0) / 1e6);
        }
        System.out.printf("%-20s size %,d  %9.1f ms%n", label, size, best);
    }

    public static void main(String[] args) {
        int n = 1_000_000;
        System.out.println(n + " adds, best of 3, on my machine:");
        bestOf3Ms("java.util.ArrayList", n, k -> {
            List<Integer> l = new ArrayList<>(); for (int i = 0; i < k; i++) l.add(i); return l.size(); });
        bestOf3Ms("MyArrayList (x2)", n, k -> {
            MyArrayList<Integer> l = new MyArrayList<>(); for (int i = 0; i < k; i++) l.add(i); return l.size(); });
        bestOf3Ms("PlusTenList (+10)", n, k -> {
            PlusTenList<Integer> l = new PlusTenList<>(); for (int i = 0; i < k; i++) l.add(i); return l.size(); });
    }
}
