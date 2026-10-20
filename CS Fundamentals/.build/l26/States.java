public class States {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        Thread sleeper = new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) { }
        });
        System.out.println("before start: " + sleeper.getState());
        sleeper.start();
        Thread.sleep(50);                    // demo only: give the thread time to get there
        System.out.println("sleeping:     " + sleeper.getState());

        Thread joiner = new Thread(() -> {
            try { sleeper.join(); } catch (InterruptedException e) { }
        });
        joiner.start(); Thread.sleep(50);
        System.out.println("joining:      " + joiner.getState());

        Thread blocked = new Thread(() -> { synchronized (lock) { } });
        synchronized (lock) {                // main holds the lock...
            blocked.start(); Thread.sleep(50);
            System.out.println("needs lock:   " + blocked.getState());
        }                                    // ...and releases it here

        Thread spinner = new Thread(() -> {
            long x = 0;
            while (!Thread.currentThread().isInterrupted()) x++;
        });
        spinner.start(); Thread.sleep(50);
        System.out.println("computing:    " + spinner.getState());
        spinner.interrupt();

        for (Thread t : new Thread[] { sleeper, joiner, blocked, spinner }) t.join();
        System.out.println("all finished: " + sleeper.getState());
    }
}
