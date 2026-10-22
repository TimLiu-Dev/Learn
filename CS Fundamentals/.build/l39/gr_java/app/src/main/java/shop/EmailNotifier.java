package shop;

public record EmailNotifier(String from) implements Notifier {
    public void send(String to, String message) { System.out.println("  [email " + from + "] to " + to + ": " + message); }
}
