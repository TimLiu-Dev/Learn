// What runs when a process is asked to stop (SIGTERM, SIGINT) or forced to (SIGKILL)?
public class Hook {
    public static void main(String[] args) throws InterruptedException {
        int seconds = args.length > 0 ? Integer.parseInt(args[0]) : 30;
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            System.out.println("hook: saving state before exit")));
        System.out.println("pid " + ProcessHandle.current().pid() + ": working for " + seconds + " s");
        try {
            for (int i = 0; i < seconds * 5; i++) Thread.sleep(200);
            System.out.println("main: finished normally");
        } finally {
            System.out.println("main: finally block");
        }
    }
}
