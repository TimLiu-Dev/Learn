import java.util.*;

// A min-heap of ints in a growable array. The root (smallest) is at index 0.
public class IntMinHeap {
    private int[] a = new int[4];
    private int size = 0;

    public int size() { return size; }

    private static int parent(int i) { return (i - 1) / 2; }
    private static int left(int i) { return 2 * i + 1; }   // right child is left(i) + 1

    private void swap(int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    public void add(int x) {
        if (size == a.length) a = Arrays.copyOf(a, size * 2);   // grow like L16's MyArrayList
        a[size] = x;                                 // 1. put it at the end (keeps the shape)
        int i = size++;
        while (i > 0 && a[i] < a[parent(i)]) {       // 2. sift up while smaller than parent
            swap(i, parent(i));
            i = parent(i);
        }
    }

    public int peek() {
        if (size == 0) throw new IllegalStateException("heap is empty");
        return a[0];
    }

    public int poll() {
        int top = peek();
        a[0] = a[--size];                            // 1. move the LAST element to the root
        int i = 0;
        while (true) {                               // 2. sift down: swap with the smaller child
            int l = left(i);
            if (l >= size) break;                    // no children: done
            int child = (l + 1 < size && a[l + 1] < a[l]) ? l + 1 : l;
            if (a[i] <= a[child]) break;             // already no bigger than both children
            swap(i, child);
            i = child;
        }
        return top;
    }

    @Override public String toString() { return Arrays.toString(Arrays.copyOf(a, size)); }

    public static void main(String[] args) {
        IntMinHeap h = new IntMinHeap();
        for (int x : new int[] {5, 1, 8, 3, 2, 9}) { h.add(x); System.out.println("add " + x + " -> " + h); }
        while (h.size() > 0) { int top = h.poll(); System.out.println("poll " + top + " -> " + h); }
        try { h.poll(); } catch (IllegalStateException e) { System.out.println("poll on empty: " + e.getMessage()); }
    }
}
