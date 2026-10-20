public class Misuse {
    static class EmailNotifier {
        void send(String to, String message) {
            System.out.println("[email] to " + to + ": " + format(message));
        }
        protected String format(String message) { return message; }
    }

    static class SmsNotifier {
        void send(String to, String message) {
            System.out.println("[sms] to " + to + ": " + format(message));
        }
        protected String format(String message) { return message; }
    }

    // "Urgent" bolted on by subclassing: one subclass per channel, same code twice
    static class UrgentEmailNotifier extends EmailNotifier {
        @Override protected String format(String message) { return "URGENT: " + super.format(message); }
    }

    static class UrgentSmsNotifier extends SmsNotifier {
        @Override protected String format(String message) { return "URGENT: " + super.format(message); }
    }

    public static void main(String[] args) {
        new UrgentEmailNotifier().send("ada", "Server down");
        new UrgentSmsNotifier().send("ada", "Server down");
    }
}
