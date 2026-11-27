import java.util.concurrent.ArrayBlockingQueue;

public class Twice {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> pool = new ArrayBlockingQueue<>(2);
        pool.add("c1");
        pool.add("c2");
        String a = pool.poll();
        pool.poll();
        String none = pool.poll();      // the pool is empty now

        pool.offer(a);
        pool.offer(a);                  // a buggy caller returns c1 a second time

        System.out.println(none + " " + pool.size() + " " + pool.poll().equals(pool.poll()));
    }
}
