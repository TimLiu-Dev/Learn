import java.util.PriorityQueue;

public class Heap {
    public static void main(String[] args) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(3);
        pq.offer(1);
        pq.offer(2);
        System.out.println(pq + " " + pq.poll());
    }
}
