import java.util.*;
import java.util.function.Consumer;

public class Practice {
    record Message(long id, String body, int attempt) {}

    // The queue delivers at least once, so the same message can arrive several times.
    static class EmailWorker {
        private final Consumer<String> sendEmail;
        private final Set<Long> done = new HashSet<>();    // ids whose email has been sent
        int skipped = 0;                                   // deliveries ignored as duplicates

        EmailWorker(Consumer<String> sendEmail) { this.sendEmail = sendEmail; }

        // Called for every delivery. If it throws, the message is not acked and comes back later.
        void handle(Message m) {
            if (done.contains(m.id())) { skipped++; return; }
            sendEmail.accept(m.body());     // if this throws, the id stays unrecorded, so the retry sends it
            done.add(m.id());               // record only after the effect has happened
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("each new message sends its email", "[welcome ada, welcome bob]", () -> {
            List<String> sent = new ArrayList<>();
            EmailWorker w = new EmailWorker(sent::add);
            w.handle(new Message(1, "welcome ada", 1));
            w.handle(new Message(2, "welcome bob", 1));
            return sent.toString();
        });
        check("a redelivered message sends nothing the second time", "[welcome ada], skipped 1", () -> {
            List<String> sent = new ArrayList<>();
            EmailWorker w = new EmailWorker(sent::add);
            w.handle(new Message(1, "welcome ada", 1));
            w.handle(new Message(1, "welcome ada", 2));
            return sent + ", skipped " + w.skipped;
        });
        check("two jobs with the same text are still two jobs", 2, () -> {
            List<String> sent = new ArrayList<>();
            EmailWorker w = new EmailWorker(sent::add);
            w.handle(new Message(7, "your book is due", 1));
            w.handle(new Message(8, "your book is due", 1));
            return sent.size();
        });
        check("a send that fails is retried on redelivery", "threw, then [welcome cy]", () -> {
            List<String> sent = new ArrayList<>();
            boolean[] down = {true};                   // the mail server is down for the first try
            EmailWorker w = new EmailWorker(s -> {
                if (down[0]) { down[0] = false; throw new IllegalStateException("mail server down"); }
                sent.add(s);
            });
            String first;
            try { w.handle(new Message(3, "welcome cy", 1)); first = "returned"; }
            catch (IllegalStateException e) { first = "threw"; }
            w.handle(new Message(3, "welcome cy", 2));
            return first + ", then " + sent;
        });
        check("an at-least-once run sends every email exactly once", "[job 1, job 2, job 3, job 4, job 5, job 6], skipped 3", () -> {
            List<String> sent = new ArrayList<>();
            EmailWorker w = new EmailWorker(sent::add);
            Map<Integer, Integer> attempts = new HashMap<>();
            for (int id : new int[]{1, 2, 3, 2, 4, 5, 6, 2, 1}) {     // #2 redelivered twice, #1 once
                int attempt = attempts.merge(id, 1, Integer::sum);
                w.handle(new Message(id, "job " + id, attempt));
            }
            return sent + ", skipped " + w.skipped;
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
