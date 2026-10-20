public class WrongLock {
    static int a = 0;
    static Integer boxed = 0;

    public static void main(String[] args) throws InterruptedException {
        Runnable work = () -> {
            for (int i = 0; i < 1_000_000; i++) {
                synchronized (new Object()) { a++; }      // a brand-new lock object every time
                synchronized (boxed) {                    // lock on the counter's own box...
                    boxed++;                              // ...which this line replaces with a new box
                }
            }
        };
        Thread t1 = new Thread(work), t2 = new Thread(work);
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("fresh lock each call: a = " + a + " (expected 2000000)");
        System.out.println("lock on boxed value:  boxed = " + boxed + " (expected 2000000)");
    }
}
