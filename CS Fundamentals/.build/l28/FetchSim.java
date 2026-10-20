import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FetchSim {
    static final int N = 1_000;
    static final int[] DELAY = new int[N];
    static final boolean[] FAILS = new boolean[N];
    static {
        Random rnd = new Random(28);                  // same "network" every run
        for (int i = 0; i < N; i++) {
            DELAY[i] = 50 + rnd.nextInt(151);         // 50..200 ms
            FAILS[i] = rnd.nextInt(100) < 2;          // about 2% fail
        }
    }

    // pretend to download page i; returns its size in bytes
    static int fetch(int i) throws IOException, InterruptedException {
        Thread.sleep(DELAY[i]);
        if (FAILS[i]) throw new IOException("HTTP 503 for /page/" + i);
        return 1_000 + i;
    }

    record Outcome(Integer bytes, String error) {}
    record Report(String name, long ms, int ok, int failed, String firstError) {}

    static Report summarise(String name, long start, List<Outcome> outcomes) {
        long ms = (System.nanoTime() - start) / 1_000_000;
        int ok = 0, failed = 0;
        String first = "";
        for (Outcome o : outcomes) {
            if (o.error() == null) ok++;
            else { failed++; if (first.isEmpty()) first = o.error(); }
        }
        return new Report(name, ms, ok, failed, first);
    }

    static List<Callable<Integer>> tasks(int count) {
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) { int id = i; tasks.add(() -> fetch(id)); }
        return tasks;
    }

    // a Future's failure arrives wrapped in ExecutionException: unwrap it (L10)
    static List<Outcome> drain(List<Future<Integer>> futures) throws InterruptedException {
        List<Outcome> out = new ArrayList<>();
        for (Future<Integer> f : futures) {
            try { out.add(new Outcome(f.get(), null)); }
            catch (ExecutionException e) { out.add(new Outcome(null, e.getCause().getMessage())); }
        }
        return out;
    }

    static Report sequential(int count) throws InterruptedException {
        long start = System.nanoTime();
        List<Outcome> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try { out.add(new Outcome(fetch(i), null)); }
            catch (IOException e) { out.add(new Outcome(null, e.getMessage())); }
        }
        return summarise("sequential (first " + count + ")", start, out);
    }

    static Report fixedPool() throws InterruptedException {
        long start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(16)) {
            return summarise("fixed pool of 16", start, drain(pool.invokeAll(tasks(N))));
        }
    }

    static Report completable() {
        long start = System.nanoTime();
        try (ExecutorService pool = Executors.newFixedThreadPool(100)) {
            List<CompletableFuture<Outcome>> futures = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                int id = i;
                futures.add(CompletableFuture
                    .supplyAsync(() -> {
                        try { return fetch(id); }
                        catch (Exception e) { throw new CompletionException(e); }
                    }, pool)
                    .handle((bytes, e) -> e == null ? new Outcome(bytes, null)
                                                    : new Outcome(null, e.getCause().getMessage())));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return summarise("CompletableFuture, 100", start, futures.stream().map(CompletableFuture::join).toList());
        }
    }

    static Report virtualThreads() throws InterruptedException {
        long start = System.nanoTime();
        try (ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor()) {
            return summarise("virtual thread per task", start, drain(vt.invokeAll(tasks(N))));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Report> reports = List.of(sequential(100), fixedPool(), completable(), virtualThreads());
        System.out.printf("%-26s %8s %5s %7s  %s%n", "version", "time", "ok", "failed", "first failure");
        for (Report r : reports) {
            System.out.printf("%-26s %6d ms %5d %7d  %s%n", r.name(), r.ms(), r.ok(), r.failed(), r.firstError());
        }
        long totalDelay = 0;
        for (int d : DELAY) totalDelay += d;
        System.out.printf("sequential, all %d: at least %.1f s (the sum of every delay)%n", N, totalDelay / 1000.0);
    }
}
