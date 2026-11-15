import java.util.concurrent.CountDownLatch;

public class ForcedRace {
    static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch bothRead = new CountDownLatch(2);
        Runnable increment = () -> {
            int seen = count;              // read
            bothRead.countDown();
            try {
                bothRead.await();          // wait until the other thread has read too
            } catch (InterruptedException e) {
                return;
            }
            count = seen + 1;              // add, write
        };
        Thread a = new Thread(increment);
        Thread b = new Thread(increment);
        a.start(); b.start();
        a.join(); b.join();
        System.out.println(count);
    }
}
