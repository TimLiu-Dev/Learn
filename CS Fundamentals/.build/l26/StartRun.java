public class StartRun {
    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> System.out.println("task runs on: " + Thread.currentThread().getName());

        new Thread(task, "worker").run();    // just a method call: runs on main
        Thread t = new Thread(task, "worker");
        t.start();                           // the JVM starts a new thread, which calls run()
        t.join();                            // main waits here until worker finishes

        Thread b = Thread.ofPlatform().name("builder-worker").start(() ->   // JDK 21+ builder
            System.out.println("builder runs on: " + Thread.currentThread().getName()));
        b.join();
        System.out.println("main done on: " + Thread.currentThread().getName());
    }
}
