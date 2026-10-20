package weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

// Java has no coroutines. Nearest equivalent: one virtual thread per fetch,
// CompletableFuture.orTimeout per request, and manual cancellation.
public class Weather {
    public record Reading(String city, int tempC) {}
    public sealed interface Outcome permits Ok, Failed {}
    public record Ok(Reading reading) implements Outcome {}
    public record Failed(String city, String reason) implements Outcome {}
    public record Progress(int done, int total, Outcome last) {}

    @FunctionalInterface
    public interface WeatherApi {
        Reading fetch(String city) throws IOException, InterruptedException;
    }

    // The same simulation as the Kotlin FakeApi, so the numbers match
    public static WeatherApi fakeApi(int seed, long minMs, long maxMs) {
        return city -> {
            var rnd = new Random(city.hashCode() ^ seed);
            Thread.sleep(rnd.nextLong(minMs, maxMs + 1));
            if (rnd.nextInt(10) == 0) throw new IOException("HTTP 503");
            return new Reading(city, rnd.nextInt(-5, 36));
        };
    }

    static Reading call(WeatherApi api, String city) {
        try {
            return api.fetch(city);
        } catch (IOException e) {
            throw new CompletionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CancellationException("interrupted");
        }
    }

    static Outcome toOutcome(String city, Reading r, Throwable e) {
        if (e == null) return new Ok(r);
        Throwable cause = e instanceof CompletionException && e.getCause() != null ? e.getCause() : e;
        if (cause instanceof TimeoutException) return new Failed(city, "timeout");
        if (cause instanceof IOException io) return new Failed(city, io.getMessage());
        return new Failed(city, cause.toString());
    }

    public static List<Outcome> dashboard(WeatherApi api, List<String> cities, long timeoutMs,
                                          Consumer<Progress> onProgress, ExecutorService exec) {
        var done = new AtomicInteger();
        var lock = new Object();
        var futures = new ArrayList<CompletableFuture<Outcome>>();
        for (String city : cities) {
            futures.add(CompletableFuture.supplyAsync(() -> call(api, city), exec)
                .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)   // does NOT stop the fetch
                .handle((reading, e) -> toOutcome(city, reading, e))
                .whenComplete((o, e) -> {
                    synchronized (lock) {                     // one progress line at a time
                        onProgress.accept(new Progress(done.incrementAndGet(), cities.size(), o));
                    }
                }));
        }
        return futures.stream().map(CompletableFuture::join).toList();
    }

    static String render(Outcome o) {
        return switch (o) {
            case Ok ok -> String.format("%-10s %d °C", ok.reading().city(), ok.reading().tempC());
            case Failed f -> String.format("%-10s FAILED (%s)", f.city(), f.reason());
        };
    }

    static final List<String> CITIES = List.of("Austin", "Berlin", "Cairo", "Delhi", "Dublin",
        "Hanoi", "Lagos", "Lima", "London", "Madrid", "Nairobi", "Oslo", "Paris", "Quito",
        "Rome", "Seoul", "Sydney", "Tokyo", "Toronto", "Vienna");

    static void run(String label, WeatherApi api, long timeoutMs) throws Exception {
        System.out.println("== " + label);
        long t0 = System.nanoTime();
        var seen = new AtomicInteger();
        var quit = new AtomicBoolean(false);
        ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
        var all = CompletableFuture.supplyAsync(() -> dashboard(api, CITIES, timeoutMs, p -> {
            if (quit.get()) return;                       // ignore late, cancelled fetches
            seen.set(p.done());
            System.out.printf("[%2d/%d] %s%n", p.done(), p.total(), render(p.last()));
        }, exec), exec);
        try {
            all.get(2, TimeUnit.SECONDS);                 // the user presses q after 2 s
            System.out.println("all " + CITIES.size() + " done in " + ms(t0) + " ms");
        } catch (TimeoutException e) {
            quit.set(true);
            System.out.println("q pressed: cancelled after " + seen.get() + "/" + CITIES.size()
                + ", " + ms(t0) + " ms");
        } finally {
            exec.shutdownNow();                           // interrupt every leftover fetch by hand
        }
    }

    static long ms(long t0) { return (System.nanoTime() - t0) / 1_000_000; }

    public static void main(String[] args) throws Exception {
        run("normal day", fakeApi(22, 100, 500), 300);
        run("slow network, user gives up", fakeApi(22, 1500, 4000), 5000);
    }
}
