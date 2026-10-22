import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

// Exercise 2 starting point: everything is reached for, nothing is passed in.
public class Ex2Before {
    static final class Mailer {                                  // a global singleton
        static final Mailer INSTANCE = new Mailer();
        private Mailer() {}
        void send(String to, String text) { System.out.println("MAIL to " + to + ": " + text); }
    }

    static final class ReminderJob {
        private final Map<String, LocalDate> loans = new LinkedHashMap<>();   // pretend this is the database
        ReminderJob() {
            loans.put("ada@example.com", LocalDate.parse("2026-10-01"));
            loans.put("alan@example.com", LocalDate.parse("2026-10-18"));
        }

        int run() {
            LocalDate today = LocalDate.now();                   // the real calendar
            int sent = 0;
            for (var e : loans.entrySet()) {
                LocalDate due = e.getValue().plusDays(14);
                if (due.isBefore(today)) { Mailer.INSTANCE.send(e.getKey(), "overdue since " + due); sent++; }
            }
            return sent;
        }
    }

    public static void main(String[] args) { System.out.println("sent " + new ReminderJob().run()); }
}
