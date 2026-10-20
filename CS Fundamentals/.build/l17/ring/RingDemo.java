public class RingDemo {
    public static void main(String[] args) {
        MyQueue<String> q = new MyQueue<>(4);
        q.offer("A");
        q.offer("B");
        q.offer("C");
        System.out.println(q.poll());   // A: first in, first out
        System.out.println(q.poll());   // B
        q.offer("D");
        q.offer("E");                   // tail wraps around to slot 0
        q.offer("F");                   // slot 1: the buffer is full again
        q.offer("G");                   // full: grow to 8, copying in queue order
        System.out.println(q.debug());
        while (q.size() > 0) System.out.print(q.poll() + " ");
        System.out.println();
    }
}
