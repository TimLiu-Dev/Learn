package shop;

import java.util.List;

// DECORATOR: records every send; stacks with RetryingNotifier in either order
public final class LoggingNotifier implements Notifier {
    private final Notifier inner;
    private final List<String> log;
    public LoggingNotifier(Notifier inner, List<String> log) { this.inner = inner; this.log = log; }
    public void send(String to, String message) {
        log.add("send to " + to);
        inner.send(to, message);
    }
}
