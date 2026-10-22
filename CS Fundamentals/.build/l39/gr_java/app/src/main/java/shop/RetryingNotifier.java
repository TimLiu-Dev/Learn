package shop;

// DECORATOR: same interface, wraps another Notifier, adds retries
public final class RetryingNotifier implements Notifier {
    private final Notifier inner;
    private final int attempts;

    public RetryingNotifier(Notifier inner, int attempts) {
        if (attempts < 1) throw new IllegalArgumentException("attempts must be >= 1");
        this.inner = inner;
        this.attempts = attempts;
    }

    public void send(String to, String message) {
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                inner.send(to, message);
                return;
            } catch (IllegalStateException e) {
                if (attempt == attempts) throw e;
                System.out.println("  retry " + (attempt + 1) + "/" + attempts + " after: " + e.getMessage());
            }
        }
    }
}
