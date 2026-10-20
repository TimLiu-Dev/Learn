// Every call on a synchronizedList is atomic. "Add if absent" is two calls, so it isn't.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyncList {
    static void addAllIfAbsent(List<Integer> list, boolean clientLock) throws InterruptedException {
        Thread[] threads = new Thread[8];
        for (int t = 0; t < 8; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < 10_000; i++) {
                    if (clientLock) {
                        synchronized (list) {                   // same lock the wrapper uses
                            if (!list.contains(i)) list.add(i);
                        }
                    } else {
                        if (!list.contains(i)) list.add(i);     // check, then act: a race
                    }
                }
            });
            threads[t].start();
        }
        for (Thread t : threads) t.join();
    }

    public static void main(String[] args) throws InterruptedException {
        List<Integer> racy = Collections.synchronizedList(new ArrayList<>());
        addAllIfAbsent(racy, false);
        System.out.println("no client lock:   size " + racy.size() + " (expected 10000)");

        List<Integer> safe = Collections.synchronizedList(new ArrayList<>());
        addAllIfAbsent(safe, true);
        System.out.println("with client lock: size " + safe.size() + " (expected 10000)");
    }
}
