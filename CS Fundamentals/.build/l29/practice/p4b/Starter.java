import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Practice {
    // A callback API you can't change. Callbacks arrive later, on another thread.
    interface Lookup { void cancel(); }
    interface PlaceListener { void onPlace(String name); }
    interface Geocoder {
        // Calls exactly one of onResult or onError. cancel() stops the lookup.
        Lookup lookup(String address, BiConsumer<Double, Double> onResult, Consumer<Exception> onError);
        // Calls the listener for every new place until it is removed.
        void addListener(PlaceListener listener);
        void removeListener(PlaceListener listener);
    }

    // Nearest equivalent of a suspend function: a future the callbacks complete.
    // Cancelling the future must cancel the lookup.
    static CompletableFuture<List<Double>> find(Geocoder geo, String address) {
        throw new UnsupportedOperationException("TODO: complete a CompletableFuture from the callbacks");
    }

    // Nearest equivalent of places().take(n): the first n places, then remove the listener.
    static CompletableFuture<List<String>> firstPlaces(Geocoder geo, int n) {
        throw new UnsupportedOperationException("TODO: collect n places, then removeListener");
    }

    // CHECKS
    // Answers "home" on another thread, fails for "mars", never answers "nowhere".
    static class FakeGeocoder implements Geocoder {
        final AtomicInteger cancelled = new AtomicInteger();
        final List<PlaceListener> listeners = new CopyOnWriteArrayList<>();
        public Lookup lookup(String address, BiConsumer<Double, Double> onResult, Consumer<Exception> onError) {
            if (address.equals("home")) new Thread(() -> onResult.accept(51.5, -0.1)).start();
            if (address.equals("mars")) new Thread(() -> onError.accept(new IllegalArgumentException("no such address: mars"))).start();
            return cancelled::incrementAndGet;
        }
        public void addListener(PlaceListener l) {
            listeners.add(l);
            new Thread(() -> { for (String p : List.of("Oslo", "Lima", "Pune", "Rome")) l.onPlace(p); }).start();
        }
        public void removeListener(PlaceListener l) { listeners.remove(l); }
    }

    public static void main(String[] args) {
        check("find returns the coordinates", List.of(51.5, -0.1),
            () -> find(new FakeGeocoder(), "home").get(2, TimeUnit.SECONDS));
        check("find fails with the API's error", "no such address: mars", () -> {
            try { find(new FakeGeocoder(), "mars").get(2, TimeUnit.SECONDS); return "no error"; }
            catch (java.util.concurrent.ExecutionException e) { return e.getCause().getMessage(); }
        });
        check("cancelling find cancels the lookup", 1, () -> {
            FakeGeocoder geo = new FakeGeocoder();
            find(geo, "nowhere").cancel(true);
            return geo.cancelled.get();
        });
        check("firstPlaces gives the first places in order", List.of("Oslo", "Lima", "Pune"),
            () -> firstPlaces(new FakeGeocoder(), 3).get(2, TimeUnit.SECONDS));
        check("firstPlaces removes its listener", 0, () -> {
            FakeGeocoder geo = new FakeGeocoder();
            firstPlaces(geo, 2).get(2, TimeUnit.SECONDS);
            return geo.listeners.size();
        });
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
