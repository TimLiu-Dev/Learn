import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Practice {
    record Change(int from, int to) {}

    // Strategy: decides whether a change is worth announcing
    interface AlertRule { boolean announce(int from, int to); }

    static final AlertRule ON_CHANGE = (from, to) -> { throw new UnsupportedOperationException("TODO: announce only a real change"); };

    static final class PriceFeed {
        private final List<Consumer<Change>> listeners = new CopyOnWriteArrayList<>();
        private final AlertRule rule;
        private int price;

        PriceFeed(int price) { this(price, ON_CHANGE); }
        PriceFeed(int price, AlertRule rule) { this.price = price; this.rule = rule; }

        AutoCloseable subscribe(Consumer<Change> listener) {
            throw new UnsupportedOperationException("TODO: add the listener; the handle removes this subscription and nothing else");
        }

        void update(int newPrice) {
            throw new UnsupportedOperationException("TODO: remember the new price; if the rule says so, tell every subscriber");
        }
    }

    // CHECKS
    // Two Recorders with the same name are EQUAL, but each keeps its own log.
    static final class Recorder implements Consumer<Change> {
        final String name;
        final List<Integer> got = new ArrayList<>();
        Recorder(String name) { this.name = name; }
        public void accept(Change c) { got.add(c.to()); }
        @Override public boolean equals(Object o) { return o instanceof Recorder r && r.name.equals(name); }
        @Override public int hashCode() { return name.hashCode(); }
    }

    public static void main(String[] args) {
        check("every subscriber hears every change", "[105, 99] [105, 99]", () -> {
            var feed = new PriceFeed(100); var a = new Recorder("a"); var b = new Recorder("b");
            feed.subscribe(a); feed.subscribe(b); feed.update(105); feed.update(99);
            return a.got + " " + b.got; });
        check("the default rule stays quiet when the price does not move", List.of(101), () -> {
            var feed = new PriceFeed(100); var a = new Recorder("a");
            feed.subscribe(a); feed.update(100); feed.update(101); feed.update(101);
            return a.got; });
        check("a rule the feed has never seen", List.of(120), () -> {
            AlertRule bigMoves = (from, to) -> Math.abs(to - from) >= 10;
            var feed = new PriceFeed(100, bigMoves); var a = new Recorder("a");
            feed.subscribe(a); feed.update(105); feed.update(120); feed.update(112);
            return a.got; });
        check("closing a handle stops that listener, not an equal one", "first [101], second []", () -> {
            var feed = new PriceFeed(100); var first = new Recorder("screen"); var second = new Recorder("screen");
            feed.subscribe(first); AutoCloseable handle = feed.subscribe(second);
            handle.close(); feed.update(101);
            return "first " + first.got + ", second " + second.got; });
        check("the same listener twice is called twice, until one handle closes", List.of(101, 101, 102), () -> {
            var feed = new PriceFeed(100); var r = new Recorder("r");
            AutoCloseable h1 = feed.subscribe(r); feed.subscribe(r);
            feed.update(101); h1.close(); feed.update(102);
            return r.got; });
        check("closing a handle twice does nothing the second time", List.of(101), () -> {
            var feed = new PriceFeed(100); var r = new Recorder("r");
            AutoCloseable h1 = feed.subscribe(r); feed.subscribe(r);
            h1.close(); h1.close(); feed.update(101);
            return r.got; });
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
