import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

// Idle and absolute timeouts, with the clock passed in so a test can move time instead of waiting for it.
public class SessionStore {
    private static final class Session {
        final String user;
        final long created;
        volatile long lastSeen;
        Session(String user, long created) { this.user = user; this.created = created; this.lastSeen = created; }
    }
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final SecureRandom rng = new SecureRandom();
    private final long idleMs, absoluteMs;
    private final LongSupplier now;

    SessionStore(long idleMs, long absoluteMs, LongSupplier now) {
        this.idleMs = idleMs;
        this.absoluteMs = absoluteMs;
        this.now = now;
    }
    SessionStore(LongSupplier now) { this(15 * 60_000L, 8 * 3_600_000L, now); }   // OWASP: 15-30 min idle, 4-8 h absolute
    SessionStore() { this(System::currentTimeMillis); }

    String create(String user) {
        var b = new byte[32];
        rng.nextBytes(b);
        var id = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        sessions.put(id, new Session(user, now.getAsLong()));
        return id;
    }

    String lookup(String id) {
        var s = sessions.get(id);
        if (s == null) return null;
        long t = now.getAsLong();
        if (t - s.lastSeen >= idleMs || t - s.created >= absoluteMs) {
            sessions.remove(id);                              // expired: forget it on the server, not just the client
            return null;
        }
        s.lastSeen = t;
        return s.user;
    }

    int size() { return sessions.size(); }

    static long clock = 0;                                    // a fake clock, in milliseconds

    public static void main(String[] args) {
        var store = new SessionStore(() -> clock);
        var a = store.create("ana");
        at(store, 10, a);                                     // active
        at(store, 24, a);                                     // 14 min idle: still fine
        at(store, 40, a);                                     // 16 min idle: gone
        clock = 0;
        var b = store.create("ben");
        for (long m = 10; m <= 470; m += 10) { clock = m * 60_000; store.lookup(b); }   // busy all day, never idle
        at(store, 470, b);
        at(store, 480, b);                                    // 8 h after login: gone, however busy
        System.out.println("sessions left: " + store.size());
    }

    static void at(SessionStore store, long minutes, String id) {
        clock = minutes * 60_000;
        System.out.printf("t=%3d min  lookup -> %s%n", minutes, store.lookup(id));
    }
}
