import java.util.ArrayList;
import java.util.List;

public class Notify {
    interface Notifier {
        void send(String to, String message);
    }

    // Shared state (a sent counter) and shared steps justify an abstract base
    static abstract class ChannelNotifier implements Notifier {
        private final String channel;
        private int sent = 0;

        ChannelNotifier(String channel) { this.channel = channel; }

        int getSent() { return sent; }

        @Override
        public final void send(String to, String message) {   // fixed steps; subclasses can't skip the count
            sent++;
            System.out.println("[" + channel + " #" + sent + "] to " + to + ": " + format(message));
        }

        protected String format(String message) { return message; }   // the one step subclasses may change
    }

    static final class EmailNotifier extends ChannelNotifier {
        EmailNotifier() { super("email"); }
    }

    static final class SmsNotifier extends ChannelNotifier {
        SmsNotifier() { super("sms"); }
        @Override
        protected String format(String message) {
            return message.length() <= 20 ? message : message.substring(0, 17) + "...";
        }
    }

    static final class PushNotifier extends ChannelNotifier {
        PushNotifier() { super("push"); }
        @Override
        protected String format(String message) { return "(!) " + message; }
    }

    // Composition: wrap ANY notifier to add behaviour, no subclass per channel
    static final class LoggingNotifier implements Notifier {
        private final Notifier inner;
        private final List<String> log;

        LoggingNotifier(Notifier inner, List<String> log) {
            this.inner = inner;
            this.log = log;
        }

        @Override
        public void send(String to, String message) {
            log.add("sending to " + to);
            inner.send(to, message);
        }
    }

    static final class UrgentNotifier implements Notifier {
        private final Notifier inner;

        UrgentNotifier(Notifier inner) { this.inner = inner; }

        @Override
        public void send(String to, String message) { inner.send(to, "URGENT: " + message); }
    }

    public static void main(String[] args) {
        List<String> log = new ArrayList<>();
        EmailNotifier email = new EmailNotifier();
        List<Notifier> channels = List.of(
                email,
                new SmsNotifier(),
                new LoggingNotifier(new PushNotifier(), log),
                new UrgentNotifier(new SmsNotifier()),
                new UrgentNotifier(new LoggingNotifier(email, log)));
        for (Notifier n : channels) n.send("ada", "Your build finished successfully");
        System.out.println(log);
        System.out.println("emails sent: " + email.getSent());
    }
}
