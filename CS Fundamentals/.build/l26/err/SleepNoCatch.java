public class SleepNoCatch {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            Thread.sleep(100);          // Runnable.run() declares no exceptions
            System.out.println("done");
        });
        t.start();
    }
}
