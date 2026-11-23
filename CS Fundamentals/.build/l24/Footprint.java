import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

// Fills three memory areas outside the heap, then waits so jcmd can look.
public class Footprint {
    record Point(int x, int y) {}

    public static void main(String[] args) throws Exception {
        Point[] points = new Point[1_000_000];               // heap: 1M small objects
        for (int i = 0; i < points.length; i++) points[i] = new Point(i, -i);

        ByteBuffer direct = ByteBuffer.allocateDirect(64 << 20); // 64 MB off-heap
        for (int i = 0; i < direct.capacity(); i += 4096) direct.put(i, (byte) 1);

        CountDownLatch never = new CountDownLatch(1);
        for (int i = 0; i < 50; i++) {                        // 50 idle threads
            Thread t = new Thread(() -> {
                try { never.await(); } catch (InterruptedException e) { }
            });
            t.setDaemon(true);
            t.start();
        }
        System.out.println("pid " + ProcessHandle.current().pid() + ", "
                + points.length + " points, " + direct.capacity() / (1 << 20) + " MB direct");
        Thread.sleep(Long.parseLong(args.length > 0 ? args[0] : "5000"));
    }
}
