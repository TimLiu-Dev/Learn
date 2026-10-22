package shop;

// FACTORY: config string in, the right Notifier out; callers never name a concrete class
public final class Notifiers {
    private Notifiers() {}

    public static Notifier from(String config) {
        String[] parts = config.split(":", 2);
        return switch (parts[0]) {
            case "email" -> new EmailNotifier(parts.length > 1 ? parts[1] : "noreply@shop.test");
            case "sms" -> new FlakySmsGateway(parts.length > 1 ? Integer.parseInt(parts[1]) : 0);
            default -> throw new IllegalArgumentException("unknown notifier: " + config);
        };
    }
}
