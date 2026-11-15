import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Practice {
    // Runs each task on its own new thread, named prefix-0, prefix-1, …
    // Waits for all of them, then returns their results in task order.
    static List<String> runAll(String prefix, List<Supplier<String>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: start one thread per task, then join them all");
    }

    // CHECKS
    static String meet(CountDownLatch arrived) {
        arrived.countDown();
        try {
            return arrived.await(2, TimeUnit.SECONDS) ? "met" : "waited alone for 2 s";
        } catch (InterruptedException e) {
            return "interrupted";
        }
    }

    static String slow(List<Thread> seen) {
        seen.add(Thread.currentThread());
        try { Thread.sleep(100); } catch (InterruptedException e) { return "interrupted"; }
        return "done";
    }

    public static void main(String[] args) {
        check("results come back in task order", List.of("a", "b", "c"),
            () -> runAll("w", List.of(() -> "a", () -> "b", () -> "c")));
        Supplier<String> myName = () -> Thread.currentThread().getName();
        check("each task runs on its own named thread", List.of("job-0", "job-1", "job-2"),
            () -> runAll("job", List.of(myName, myName, myName)));
        CountDownLatch arrived = new CountDownLatch(3);
        Supplier<String> meet = () -> meet(arrived);
        check("the tasks really run at the same time", List.of("met", "met", "met"),
            () -> runAll("m", List.of(meet, meet, meet)));
        List<Thread> seen = Collections.synchronizedList(new ArrayList<>());
        check("every thread has finished when runAll returns", List.of(Thread.State.TERMINATED, Thread.State.TERMINATED),
            () -> {
                runAll("s", List.of(() -> slow(seen), () -> slow(seen)));
                return seen.stream().map(Thread::getState).toList();
            });
        check("no tasks means an empty list", List.of(), () -> runAll("x", List.of()));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
