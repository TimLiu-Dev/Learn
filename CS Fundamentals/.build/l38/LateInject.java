import java.time.Clock;

// Setter injection: the dependency is assigned AFTER construction... if someone remembers.
public class LateInject {
    static final class ReminderService {
        private Clock clock;

        void setClock(Clock clock) { this.clock = clock; }

        String stamp() { return "reminder at " + clock.instant(); }
    }

    public static void main(String[] args) {
        ReminderService wired = new ReminderService();
        wired.setClock(Clock.systemUTC());
        System.out.println(wired.stamp().substring(0, 23) + "...");
        ReminderService forgotten = new ReminderService();   // compiles fine
        System.out.println(forgotten.stamp());               // fails only when it runs
    }
}
