import java.util.List;
import java.util.concurrent.*;

// Nearest equivalent: a hand-written Flow.Subscriber does filter, map,
// take and error handling itself.
public class Ex3 {
    static volatile Double latest;                   // what a UI would observe

    static void watch(Integer limit) throws Exception {
        var finished = new CompletableFuture<Void>();
        var pub = new SubmissionPublisher<Integer>();
        pub.subscribe(new Flow.Subscriber<>() {
            Flow.Subscription sub;
            int taken = 0;
            public void onSubscribe(Flow.Subscription s) { sub = s; s.request(Long.MAX_VALUE); }
            public void onNext(Integer c) {
                if (c == -999 || finished.isDone()) return;              // filter
                double f = c * 9.0 / 5 + 32;                              // map
                latest = f;
                System.out.printf("  %.1f °F%n", f);
                if (limit != null && ++taken == limit) {                  // take
                    sub.cancel();
                    System.out.println("  completed");
                    finished.complete(null);
                }
            }
            public void onError(Throwable t) {                            // catch
                System.out.println("  sensor error: " + t.getMessage());
                System.out.println("  completed");
                finished.complete(null);
            }
            public void onComplete() { System.out.println("  completed"); finished.complete(null); }
        });
        Thread.ofVirtual().start(() -> {
            for (int c : List.of(21, 22, -999, 23, 24)) { sleep(100); pub.submit(c); }
            sleep(100);
            pub.closeExceptionally(new IllegalStateException("sensor unplugged"));
        });
        finished.get();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("take(3):"); watch(3);
        System.out.println("no limit:"); watch(null);
        System.out.println("latest = " + latest);
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
