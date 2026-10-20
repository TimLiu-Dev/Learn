import java.util.concurrent.*;

// Nearest equivalent of a SharedFlow: a SubmissionPublisher is hot,
// and every current subscriber receives each item.
public class Broadcast {
    public static void main(String[] args) throws Exception {
        try (var events = new SubmissionPublisher<String>()) {
            var a = events.consume(e -> System.out.println("A got " + e));
            var b = events.consume(e -> System.out.println("B got " + e));
            events.submit("saved");
            Thread.sleep(50);
            events.submit("synced");
            events.close();
            CompletableFuture.allOf(a, b).join();
        }
        // The JDK has no StateFlow-like "current value" stream.
    }
}
