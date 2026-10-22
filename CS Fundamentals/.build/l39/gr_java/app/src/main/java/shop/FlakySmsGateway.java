package shop;

// A simulated SMS gateway that times out for its first `failures` calls
public final class FlakySmsGateway implements Notifier {
    private int failures;
    public FlakySmsGateway(int failures) { this.failures = failures; }
    public void send(String to, String message) {
        if (failures-- > 0) throw new IllegalStateException("gateway timeout");
        System.out.println("  [sms] to " + to + ": " + message);
    }
}
