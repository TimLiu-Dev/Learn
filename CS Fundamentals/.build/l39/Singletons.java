import java.time.*;

public class Singletons {
    // Effective Java Item 3: an enum is the simplest bullet-proof singleton
    enum IdGenerator {
        INSTANCE;
        private long next = 1;
        synchronized long nextId() { return next++; }
    }

    // Lazy holder (L27): Holder is initialised on first use, and class init is thread-safe
    static final class Config {
        private Config() { System.out.println("  (Config loading...)"); }
        private static final class Holder { static final Config INSTANCE = new Config(); }
        static Config get() { return Holder.INSTANCE; }
        int loanDays() { return 21; }
    }

    // Hidden dependencies: global config and the real clock. A test can't control either.
    static boolean isOverdueHidden(LocalDate loaned) {
        return loaned.plusDays(Config.get().loanDays()).isBefore(LocalDate.now());
    }

    // Injected (L38): the dependencies are parameters, so a test passes fixed ones
    static boolean isOverdue(LocalDate loaned, int loanDays, Clock clock) {
        return loaned.plusDays(loanDays).isBefore(LocalDate.now(clock));
    }

    public static void main(String[] args) {
        System.out.println(IdGenerator.INSTANCE.nextId() + " " + IdGenerator.INSTANCE.nextId());
        System.out.println("before first use");
        System.out.println("loan days: " + Config.get().loanDays());
        System.out.println("same instance: " + (Config.get() == Config.get()));
        Clock nov1 = Clock.fixed(Instant.parse("2026-11-01T00:00:00Z"), ZoneOffset.UTC);
        System.out.println("loaned Oct 1, overdue on Nov 1? " + isOverdue(LocalDate.parse("2026-10-01"), 21, nov1));
        System.out.println("loaned Oct 20, overdue on Nov 1? " + isOverdue(LocalDate.parse("2026-10-20"), 21, nov1));
    }
}
