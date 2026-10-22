import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Exercise 2 solution: every dependency arrives through the constructor.
public class Ex2 {
    @FunctionalInterface interface Mailer { void send(String to, String text); }
    @FunctionalInterface interface OpenLoans { Map<String, LocalDate> all(); }

    static final class ReminderJob {
        private final OpenLoans loans;
        private final Mailer mailer;
        private final Clock clock;

        ReminderJob(OpenLoans loans, Mailer mailer, Clock clock) {
            this.loans = loans;
            this.mailer = mailer;
            this.clock = clock;
        }

        int run() {
            LocalDate today = LocalDate.now(clock);
            int sent = 0;
            for (var e : loans.all().entrySet()) {
                LocalDate due = e.getValue().plusDays(14);
                if (due.isBefore(today)) { mailer.send(e.getKey(), "overdue since " + due); sent++; }
            }
            return sent;
        }
    }

    static final class RecordingMailer implements Mailer {     // a spy: records what it was asked to do
        final List<String> sent = new ArrayList<>();
        public void send(String to, String text) { sent.add(to + ": " + text); }
    }

    public static void main(String[] args) {
        Map<String, LocalDate> data = new LinkedHashMap<>();
        data.put("ada@example.com", LocalDate.parse("2026-10-01"));
        data.put("alan@example.com", LocalDate.parse("2026-10-18"));
        OpenLoans loans = () -> data;

        // Production wiring (composition root): the real clock, mail printed to the console.
        Mailer console = (to, text) -> System.out.println("MAIL to " + to + ": " + text);
        System.out.println("production: sent " + new ReminderJob(loans, console, Clock.systemUTC()).run());

        // Test wiring: a fixed day and a recording mailer. Same answer on any day you run it.
        RecordingMailer spy = new RecordingMailer();
        Clock nov5 = Clock.fixed(Instant.parse("2026-11-05T08:00:00Z"), ZoneOffset.UTC);
        System.out.println("test on 2026-11-05: sent " + new ReminderJob(loans, spy, nov5).run() + " -> " + spy.sent);
    }
}
