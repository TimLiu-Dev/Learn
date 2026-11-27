import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Practice {
    static class FakeClock { long now = 0; }

    // A pool of named connections. A borrower who finds none free joins a line, but only
    // waits until its deadline; then it gives up (HikariCP calls this connectionTimeout).
    static class Pool {
        private record Waiter(String who, long deadline) { }

        private final FakeClock clock;
        private final ArrayDeque<String> free = new ArrayDeque<>();
        private final Set<String> lent = new HashSet<>();
        private final ArrayDeque<Waiter> waiters = new ArrayDeque<>();
        final List<String> log = new ArrayList<>();

        Pool(int size, FakeClock clock) {
            this.clock = clock;
            for (int i = 1; i <= size; i++) free.addLast("c" + i);
        }

        void borrow(String who, long timeoutMs) {
            String conn = free.pollFirst();
            if (conn != null) {
                lent.add(conn);
                log.add(who + " got " + conn);
            } else {
                waiters.addLast(new Waiter(who, clock.now + timeoutMs));
                log.add(who + " waits");
            }
        }

        // Deadlines aren't in line order (each borrower picks its own timeout), so check every waiter.
        private void expire() {
            var it = waiters.iterator();
            while (it.hasNext()) {
                Waiter w = it.next();
                if (clock.now >= w.deadline()) {
                    it.remove();
                    log.add(w.who() + " timed out");
                }
            }
        }

        void release(String conn) {
            if (!lent.remove(conn)) throw new IllegalStateException(conn + " is not lent out");
            expire();                                  // the clock may have moved since anyone looked
            Waiter next = waiters.pollFirst();
            if (next == null) {
                free.addLast(conn);
            } else {
                lent.add(conn);
                log.add(next.who() + " got " + conn);
            }
        }

        void advance(long ms) {
            clock.now += ms;
            expire();
        }
    }

    // CHECKS
    static String log(Pool p) { return String.join(", ", p.log); }

    public static void main(String[] args) {
        check("free connections are handed out at once", "a got c1, b got c2", () -> {
            Pool p = new Pool(2, new FakeClock());
            p.borrow("a", 100);
            p.borrow("b", 100);
            return log(p);
        });
        check("the longest waiter is served first", "a got c1, b waits, c waits, b got c1, c got c1", () -> {
            Pool p = new Pool(1, new FakeClock());
            p.borrow("a", 100);
            p.borrow("b", 100);
            p.borrow("c", 100);
            p.release("c1");
            p.release("c1");
            return log(p);
        });
        check("a waiter gives up when its deadline comes", "a got c1, b waits | a got c1, b waits, b timed out, d got c1", () -> {
            Pool p = new Pool(1, new FakeClock());
            p.borrow("a", 100);
            p.borrow("b", 50);
            p.advance(49);
            String at49 = log(p);
            p.advance(1);
            p.release("c1");
            p.borrow("d", 100);
            return at49 + " | " + log(p);
        });
        check("a short timeout behind a long one still expires on time", "a got c1, b waits, c waits, c timed out, b got c1", () -> {
            Pool p = new Pool(1, new FakeClock());
            p.borrow("a", 100);
            p.borrow("b", 100);
            p.borrow("c", 30);
            p.advance(30);
            p.release("c1");
            return log(p);
        });
        check("release checks deadlines even if nobody called advance", "a got c1, b waits, b timed out, e got c1", () -> {
            FakeClock clock = new FakeClock();
            Pool p = new Pool(1, clock);
            p.borrow("a", 100);
            p.borrow("b", 50);
            clock.now = 80;
            p.release("c1");
            p.borrow("e", 100);
            return log(p);
        });
        check("releasing a connection twice is refused", "refused | a got c1, x got c1, y waits", () -> {
            Pool p = new Pool(1, new FakeClock());
            p.borrow("a", 100);
            p.release("c1");
            String second;
            try { p.release("c1"); second = "accepted"; } catch (IllegalStateException e) { second = "refused"; }
            p.borrow("x", 100);
            p.borrow("y", 100);
            return second + " | " + log(p);
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
