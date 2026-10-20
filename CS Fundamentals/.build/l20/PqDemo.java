import java.util.*;

public class PqDemo {
    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();      // a min-heap by natural order
        for (int x : new int[] {5, 1, 8, 3, 2, 9}) pq.add(x);
        System.out.println("println(pq): " + pq);              // the backing ARRAY, not sorted order
        System.out.println("peek:        " + pq.peek());
        System.out.print("poll order:  ");
        while (!pq.isEmpty()) System.out.print(pq.poll() + " ");
        System.out.println();

        // Max-heap: reverse the comparison.
        PriorityQueue<Integer> maxPq = new PriorityQueue<>(Comparator.reverseOrder());
        maxPq.addAll(List.of(5, 1, 8, 3, 2, 9));
        System.out.println("max first:   " + maxPq.poll() + " " + maxPq.poll() + " " + maxPq.poll());

        // Any ordering you can write as a comparator: longest word first.
        PriorityQueue<String> byLength = new PriorityQueue<>(
            Comparator.comparingInt(String::length).reversed().thenComparing(Comparator.naturalOrder()));
        byLength.addAll(List.of("fig", "banana", "kiwi", "cherry"));
        System.out.println("longest:     " + byLength.poll() + " " + byLength.poll());
    }
}
