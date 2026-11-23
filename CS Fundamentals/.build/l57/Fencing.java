// A lease-based lock, a client that pauses, and storage with and without fencing tokens.
// Virtual time: "now" is a number passed in, so nothing sleeps and every run is identical.
public class Fencing {
    static class LockService {
        private final long ttlMs;
        private String holder = null;
        private long expiresAt = 0;
        private long nextToken = 33;                     // grows by one on every grant, never reused

        LockService(long ttlMs) { this.ttlMs = ttlMs; }

        // A token if the lease is free (or has expired), null if someone else holds it.
        Long acquire(String client, long now) {
            if (holder != null && now < expiresAt) return null;
            holder = client;
            expiresAt = now + ttlMs;
            return nextToken++;
        }

        boolean holds(String client, long now) { return client.equals(holder) && now < expiresAt; }
    }

    static class Storage {
        private final boolean fenced;
        private String value = "(empty)";
        private long highestToken = 0;

        Storage(boolean fenced) { this.fenced = fenced; }

        String value() { return value; }

        void write(String client, long token, String newValue, long now) {
            if (fenced && token < highestToken) {
                System.out.printf("  t=%5d  storage REJECTS %s: token %d < %d already seen%n", now, client, token, highestToken);
                return;
            }
            highestToken = Math.max(highestToken, token);
            value = newValue;
            System.out.printf("  t=%5d  storage accepts %s's write (token %d): %s%n", now, client, token, newValue);
        }
    }

    static void scenario(boolean fenced) {
        System.out.println(fenced ? "WITH fencing tokens" : "WITHOUT fencing tokens");
        LockService lock = new LockService(10_000);
        Storage storage = new Storage(fenced);

        long t1 = lock.acquire("client1", 0);
        System.out.println("  t=    0  client1 acquires the lease, token " + t1 + ", valid until t=10000");
        System.out.println("  t= 1000  client1 checks the lease before writing: holds = " + lock.holds("client1", 1_000));
        System.out.println("  t= 1000  client1 stops for a 15 s GC pause, right after its check");
        System.out.println("  t= 9000  client2 tries: token " + lock.acquire("client2", 9_000) + " (lease still held)");
        long t2 = lock.acquire("client2", 11_000);
        System.out.println("  t=11000  client2 acquires the expired lease, token " + t2);
        storage.write("client2", t2, "report v2 (client2)", 12_000);
        // client1 wakes up. Its check said "holds = true", so it writes what it prepared before the pause.
        storage.write("client1", t1, "report v1 (client1, stale)", 16_000);
        System.out.println("  final value: " + storage.value());
        System.out.println();
    }

    public static void main(String[] args) {
        scenario(false);
        scenario(true);
    }
}
