import java.util.List;

// OCP two ways. L8's notifier is open for new CHANNELS; a sealed type is open for new OPERATIONS.
public class Ocp {
    // ---------- Interface: add channels without touching existing code ----------
    @FunctionalInterface
    interface Notifier {
        void send(String to, String message);
    }

    static final class Broadcaster {                         // never edited again
        private final List<Notifier> channels;
        Broadcaster(List<Notifier> channels) { this.channels = channels; }
        void announce(String to, String message) {
            for (Notifier n : channels) n.send(to, message);
        }
    }

    static final class EmailNotifier implements Notifier {
        public void send(String to, String message) { System.out.println("  email to " + to + ": " + message); }
    }

    static final class SmsNotifier implements Notifier {
        public void send(String to, String message) {
            System.out.println("  sms to " + to + ": " + message.substring(0, 17) + "...");
        }
    }

    // Added months later: a new class, zero edits to Broadcaster, EmailNotifier or SmsNotifier.
    static final class SlackNotifier implements Notifier {
        private final String room;
        SlackNotifier(String room) { this.room = room; }
        public void send(String to, String message) { System.out.println("  slack #" + room + ": @" + to + " " + message); }
    }

    // ---------- Sealed type: add operations without touching existing code ----------
    sealed interface Channel permits Email, Sms {}
    record Email() implements Channel {}
    record Sms(int maxChars) implements Channel {}

    static int costCents(Channel c) {
        return switch (c) {                                  // no `default`: the compiler checks every case
            case Email e -> 0;
            case Sms s -> 5;
        };
    }

    // Added months later: a new operation is one new method. No class was edited.
    static String describe(Channel c) {
        return switch (c) {
            case Email e -> "email";
            case Sms s -> "sms (max " + s.maxChars() + " chars)";
        };
    }

    public static void main(String[] args) {
        System.out.println("interface, three channels:");
        new Broadcaster(List.of(new EmailNotifier(), new SmsNotifier(), new SlackNotifier("library")))
                .announce("ada", "Your reserved book is ready");
        System.out.println("sealed, two operations:");
        for (Channel c : List.<Channel>of(new Email(), new Sms(160)))
            System.out.println("  " + describe(c) + " costs " + costCents(c) + " cents");
    }
}
