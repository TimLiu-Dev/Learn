// Allocates ~4 GB of short-lived objects while keeping a small window alive.
public class Churn {
    record Order(long id, String customer, int[] items) {}

    public static void main(String[] args) {
        Order[] recent = new Order[100_000];       // the "live" window: 100k orders
        long checksum = 0;
        long start = System.nanoTime();
        for (long i = 0; i < 20_000_000; i++) {
            Order o = new Order(i, "customer-" + (i % 1000), new int[16]);
            recent[(int) (i % recent.length)] = o; // older orders become garbage
            checksum += o.items().length;
        }
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("checksum " + checksum + " in " + ms + " ms");
        System.gc();                                // only a request (see below)
    }
}
