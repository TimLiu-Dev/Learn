import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// usage: ManySleeps <virtual|pool200|platform> <tasks>
public class ManySleeps {
    public static void main(String[] args) {
        String mode = args[0];
        int tasks = Integer.parseInt(args[1]);
        ExecutorService executor = switch (mode) {
            case "virtual" -> Executors.newVirtualThreadPerTaskExecutor();
            case "pool200" -> Executors.newFixedThreadPool(200);
            case "platform" -> Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
            default -> throw new IllegalArgumentException("unknown mode " + mode);
        };
        long start = System.nanoTime();
        try (executor) {
            for (int i = 0; i < tasks; i++) {
                executor.submit(() -> {
                    Thread.sleep(1000);                       // every task blocks for 1 s
                    return null;
                });
            }
        }                                                     // close() waits for all of them
        double secs = (System.nanoTime() - start) / 1e9;
        int peak = ManagementFactory.getThreadMXBean().getPeakThreadCount();
        System.out.printf("%-8s %,9d tasks: %6.2f s   peak platform threads: %d%n", mode, tasks, secs, peak);
    }
}
