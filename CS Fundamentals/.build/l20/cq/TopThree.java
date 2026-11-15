import java.util.*;

public class TopThree {
    public static void main(String[] args) {
        List<Integer> scores = List.of(42, 7, 99, 15, 63, 88, 23, 71, 5, 94);
        PriorityQueue<Integer> heap = new PriorityQueue<>(Comparator.reverseOrder());
        for (int x : scores) {
            heap.add(x);
            if (heap.size() > 3) heap.poll();
        }
        List<Integer> top = new ArrayList<>(heap);
        Collections.sort(top);
        System.out.println(top);
    }
}
