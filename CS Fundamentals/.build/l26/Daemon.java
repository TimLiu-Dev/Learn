public class Daemon {
    public static void main(String[] args) throws InterruptedException {
        Thread hb = new Thread(() -> {
            while (true) {                   // never ends on its own
                System.out.println("  heartbeat");
                try { Thread.sleep(100); } catch (InterruptedException e) { return; }
            }
        }, "heartbeat");
        hb.setDaemon(true);                  // must be set before start()
        hb.start();
        Thread.sleep(350);
        System.out.println("main ends; only a daemon thread is left, so the JVM exits");
    }
}
