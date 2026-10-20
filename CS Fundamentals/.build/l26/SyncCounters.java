public class SyncCounters {
    static class SyncCounter {
        private int count;
        synchronized void increment() { count++; }                  // lock = this object
        synchronized void addTwice() { increment(); increment(); }  // re-enters the lock it holds
        synchronized int get() { return count; }
    }

    static class BlockCounter {
        private final Object lock = new Object();                   // a private lock nobody else can grab
        private int count;
        void increment() { synchronized (lock) { count++; } }
        int get() { synchronized (lock) { return count; } }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int run = 1; run <= 3; run++) {
            SyncCounter s = new SyncCounter(); BlockCounter b = new BlockCounter();
            Runnable work = () -> { for (int i = 0; i < 1_000_000; i++) { s.increment(); b.increment(); } };
            Thread t1 = new Thread(work), t2 = new Thread(work);
            t1.start(); t2.start(); t1.join(); t2.join();
            System.out.println("run " + run + ": synchronized method = " + s.get() + ", synchronized (lock) = " + b.get());
        }
        SyncCounter s = new SyncCounter();
        s.addTwice();
        System.out.println("reentrant: addTwice() -> " + s.get());
    }
}
