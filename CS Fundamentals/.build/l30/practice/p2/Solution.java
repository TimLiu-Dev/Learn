import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

public class Practice {
    record Hit(List<String> addresses, long ttlLeft) { }

    static class DnsCache {
        private record Entry(List<String> addresses, long expiresAt) { }

        private final LongSupplier clock;
        private final Long fixedTtl;
        private final Map<String, Entry> entries = new HashMap<>();

        DnsCache(LongSupplier clock, Long fixedTtl) { this.clock = clock; this.fixedTtl = fixedTtl; }

        void store(String name, List<String> addresses, long ttl) {
            // Store the deadline, not the TTL: then "seconds left" is one subtraction.
            long keep = fixedTtl != null ? fixedTtl : ttl;
            entries.put(name, new Entry(addresses, clock.getAsLong() + keep));
        }

        Hit lookup(String name) {
            Entry entry = entries.get(name);
            if (entry == null) return null;
            long left = entry.expiresAt() - clock.getAsLong();
            if (left <= 0) {                    // at 0 seconds left the record must be discarded
                entries.remove(name);
                return null;
            }
            return new Hit(entry.addresses(), left);
        }
    }

    // CHECKS
    static final List<String> EXAMPLE = List.of("104.20.23.154", "172.66.147.243");
    static long now = 1_000;

    public static void main(String[] args) {
        check("a fresh answer has its full TTL", new Hit(EXAMPLE, 300), () -> {
            DnsCache cache = new DnsCache(() -> now, null);
            cache.store("example.com", EXAMPLE, 300);
            return cache.lookup("example.com");
        });
        check("the TTL counts down like dig", new Hit(EXAMPLE, 162), () -> {
            DnsCache cache = new DnsCache(() -> now, null);
            cache.store("example.com", EXAMPLE, 169);
            now += 7;
            return cache.lookup("example.com");
        });
        check("an answer is gone when its TTL runs out", Arrays.asList(new Hit(EXAMPLE, 1), null), () -> {
            DnsCache cache = new DnsCache(() -> now, null);
            cache.store("example.com", EXAMPLE, 300);
            now += 299;
            Hit before = cache.lookup("example.com");
            now += 1;
            return Arrays.asList(before, cache.lookup("example.com"));
        });
        check("an unknown name is a miss", null, () -> {
            DnsCache cache = new DnsCache(() -> now, null);
            cache.store("example.com", EXAMPLE, 300);
            return cache.lookup("example.org");
        });
        check("storing again restarts the countdown", new Hit(List.of("172.66.147.243"), 300), () -> {
            DnsCache cache = new DnsCache(() -> now, null);
            cache.store("example.com", EXAMPLE, 300);
            now += 200;
            cache.store("example.com", List.of("172.66.147.243"), 300);
            return cache.lookup("example.com");
        });
        check("a JVM-style cache ignores the record's TTL", Arrays.asList(new Hit(List.of("10.0.0.5"), 10), null), () -> {
            DnsCache jvm = new DnsCache(() -> now, 30L);
            jvm.store("db.internal", List.of("10.0.0.5"), 5);
            jvm.store("example.com", EXAMPLE, 3600);
            now += 20;
            Hit stale = jvm.lookup("db.internal");
            now += 10;
            return Arrays.asList(stale, jvm.lookup("example.com"));
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
