public class Interrupts {
    public static void main(String[] args) throws InterruptedException {
        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(10_000);
                System.out.println("woke up normally");
            } catch (InterruptedException e) {   // checked: the compiler insists on this catch
                System.out.println("sleeper: interrupted (" + e.getMessage() + "), cleaning up and stopping");
            }
        }, "sleeper");
        sleeper.start();
        Thread.sleep(100);
        sleeper.interrupt();                     // a polite request, not a kill
        sleeper.join();

        Thread crasher = new Thread(() -> { throw new IllegalStateException("boom"); }, "crasher");
        crasher.start();
        crasher.join();
        System.out.println("main is still alive; crasher.isAlive = " + crasher.isAlive());

        Thread handled = new Thread(() -> { throw new IllegalStateException("bang"); });
        handled.setUncaughtExceptionHandler((t, e) -> System.out.println("handler: " + t.getName() + " died with " + e.getMessage()));
        handled.start();
        handled.join();
    }
}
