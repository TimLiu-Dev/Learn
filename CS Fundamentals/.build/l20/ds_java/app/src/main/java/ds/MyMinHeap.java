package ds;

import java.util.Arrays;

// A min-heap in a growable array, like java.util.PriorityQueue (natural ordering).
public class MyMinHeap<T extends Comparable<T>> {
    // T erases to Comparable, so the array must really be a Comparable[] (not Object[]).
    @SuppressWarnings("unchecked")
    private T[] a = (T[]) new Comparable[4];
    private int size = 0;

    public int size() { return size; }

    private void swap(int i, int j) { T t = a[i]; a[i] = a[j]; a[j] = t; }

    public void add(T x) {
        if (size == a.length) a = Arrays.copyOf(a, size * 2);
        a[size] = x;
        int i = size++;
        while (i > 0 && a[i].compareTo(a[(i - 1) / 2]) < 0) {   // sift up
            swap(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    // Like PriorityQueue: null when empty instead of throwing.
    public T peek() { return size == 0 ? null : a[0]; }

    public T poll() {
        if (size == 0) return null;
        T top = a[0];
        a[0] = a[--size];
        a[size] = null;                                  // don't keep a stale reference (L4/L24)
        int i = 0;
        while (true) {                                   // sift down
            int l = 2 * i + 1;
            if (l >= size) break;
            int child = (l + 1 < size && a[l + 1].compareTo(a[l]) < 0) ? l + 1 : l;
            if (a[i].compareTo(a[child]) <= 0) break;
            swap(i, child);
            i = child;
        }
        return top;
    }
}
