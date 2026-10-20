import java.util.*;

public class QueueApi {
    public static void main(String[] args) {
        Queue<String> q = new ArrayDeque<>();
        q.offer("A");                               // add at the tail
        q.offer("B");
        System.out.println(q.peek() + " " + q.poll() + " " + q.poll());  // look, take, take
        System.out.println(q.poll() + " " + q.peek());                   // empty: special value null
        try { q.remove(); } catch (NoSuchElementException e) { System.out.println("remove(): " + e); }
        try { q.element(); } catch (NoSuchElementException e) { System.out.println("element(): " + e); }
        try { q.offer(null); } catch (NullPointerException e) { System.out.println("offer(null): NPE"); }

        Deque<Integer> d = new ArrayDeque<>();     // a deque works at both ends
        d.offerFirst(2); d.offerFirst(1); d.offerLast(3);
        System.out.println(d + " first=" + d.peekFirst() + " last=" + d.peekLast());
    }
}
