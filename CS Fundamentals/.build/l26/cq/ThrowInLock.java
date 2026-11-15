public class ThrowInLock {
    static final Object lock = new Object();
    static int n = 0;

    static void risky() {
        synchronized (lock) {
            n++;
            throw new IllegalStateException("oops");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            risky();
        } catch (IllegalStateException e) {
            System.out.print("caught ");
        }
        Thread t = new Thread(() -> {
            synchronized (lock) { n++; }
        });
        t.start();
        t.join();
        System.out.println(n);
    }
}
