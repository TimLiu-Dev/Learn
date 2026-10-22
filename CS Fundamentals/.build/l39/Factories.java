import java.math.BigDecimal;
import java.util.*;

public class Factories {
    // Static factory methods instead of public constructors (Effective Java Item 1)
    static final class Money {
        private static final Money ZERO = new Money(0);
        private final long cents;
        private Money(long cents) { this.cents = cents; }          // private: callers must use a factory

        static Money ofCents(long cents) { return cents == 0 ? ZERO : new Money(cents); }   // can reuse instances
        static Money parse(String s) {                             // a descriptive name a constructor can't have
            return ofCents(new BigDecimal(s).movePointRight(2).longValueExact());
        }
        @Override public String toString() { return String.format("$%d.%02d", cents / 100, cents % 100); }
    }

    // A factory can return any implementation; callers only see the interface
    interface Notifier { void send(String to, String msg); }
    record EmailNotifier(String from) implements Notifier {
        public void send(String to, String msg) { System.out.println("email " + from + " -> " + to + ": " + msg); }
    }
    record SmsNotifier() implements Notifier {
        public void send(String to, String msg) { System.out.println("sms -> " + to + ": " + msg); }
    }

    static Notifier notifierFrom(String config) {
        String[] parts = config.split(":", 2);
        return switch (parts[0]) {
            case "email" -> new EmailNotifier(parts.length > 1 ? parts[1] : "noreply@library.test");
            case "sms" -> new SmsNotifier();
            default -> throw new IllegalArgumentException("unknown notifier: " + config);
        };
    }

    public static void main(String[] args) {
        System.out.println(Money.parse("12.50") + " " + Money.ofCents(99));
        System.out.println("zero reused: " + (Money.ofCents(0) == Money.ofCents(0)));
        for (String cfg : List.of("email:desk@library.test", "sms")) notifierFrom(cfg).send("ada", "Dune is due");
        try { notifierFrom("pigeon"); } catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }

        // Factories you already use: valueOf caches -128..127 (L5); List.of returns an unmodifiable list
        System.out.println("valueOf(127) == valueOf(127): " + (Integer.valueOf(127) == Integer.valueOf(127)));
        System.out.println("valueOf(128) == valueOf(128): " + (Integer.valueOf(128) == Integer.valueOf(128)));
        try { List.of("a", "b").add("c"); }
        catch (UnsupportedOperationException e) { System.out.println("List.of(...).add -> " + e.getClass().getSimpleName()); }
    }
}
