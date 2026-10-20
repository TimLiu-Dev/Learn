import java.util.*;

public class TopK {
    // The k largest values using a min-heap that never holds more than k items.
    // The heap's root is the SMALLEST of the current top k: the one to evict.
    static List<Integer> topK(List<Integer> nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int x : nums) {
            heap.add(x);
            if (heap.size() > k) heap.poll();        // drop the smallest: O(log k)
        }
        List<Integer> out = new ArrayList<>(heap);
        out.sort(Comparator.reverseOrder());
        return out;
    }

    public static void main(String[] args) {
        List<Integer> scores = List.of(42, 7, 99, 15, 63, 88, 23, 71, 5, 94);
        System.out.println("top 3: " + topK(scores, 3));
        System.out.println("top 1: " + topK(scores, 1));
        System.out.println("top 20 of 10: " + topK(scores, 20));
    }
}
