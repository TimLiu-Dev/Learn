package weather;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import weather.Weather.*;

class WeatherTest {
    // A scripted API: no randomness, exact delays. No virtual time in Java,
    // so these tests really wait.
    final WeatherApi api = city -> switch (city) {
        case "Slow" -> { Thread.sleep(10_000); yield new Reading(city, 0); }
        case "Bad" -> { Thread.sleep(50); throw new IOException("HTTP 503"); }
        default -> { Thread.sleep(100); yield new Reading(city, 20); }
    };

    @Test
    void slowCityTimesOutBadCityFailsRestSucceed() {
        ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
        try {
            var dones = new ArrayList<Integer>();
            long t0 = System.nanoTime();
            List<Outcome> outcomes = Weather.dashboard(api, List.of("A", "Slow", "Bad", "B"), 300,
                p -> dones.add(p.done()), exec);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            assertEquals(new Ok(new Reading("A", 20)), outcomes.get(0));
            assertEquals(new Failed("Slow", "timeout"), outcomes.get(1));
            assertEquals(new Failed("Bad", "HTTP 503"), outcomes.get(2));
            assertEquals(List.of(1, 2, 3, 4), dones);
            assertTrue(ms >= 300 && ms < 1000, "took " + ms + " ms");  // concurrent, real time
        } finally {
            exec.shutdownNow();     // "Slow" is still sleeping: interrupt it
        }
    }

    @Test
    void pressingQCancelsAtTwoSeconds() {
        WeatherApi slowApi = city -> { Thread.sleep(5_000); return new Reading(city, 1); };
        ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
        try {
            var all = CompletableFuture.supplyAsync(
                () -> Weather.dashboard(slowApi, Weather.CITIES, 10_000, p -> fail("no progress expected"), exec),
                exec);
            assertThrows(TimeoutException.class, () -> all.get(2, TimeUnit.SECONDS));
        } finally {
            exec.shutdownNow();
        }
    }
}
