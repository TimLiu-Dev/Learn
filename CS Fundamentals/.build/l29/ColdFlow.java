import java.util.concurrent.*;
import java.util.concurrent.Flow.*;

// Nearest equivalent: java.util.concurrent.Flow (the Reactive Streams
// interfaces) with SubmissionPublisher, the JDK's one implementation.
public class ColdFlow {
    public static void main(String[] args) throws Exception {
        var done = new CountDownLatch(1);
        try (var pub = new SubmissionPublisher<Integer>()) {
            pub.subscribe(new Subscriber<>() {
                Subscription sub;
                public void onSubscribe(Subscription s) { sub = s; s.request(1); }
                public void onNext(Integer item) {
                    System.out.println("received " + item);
                    sub.request(1);                // backpressure: ask for one more
                }
                public void onError(Throwable t) { t.printStackTrace(); }
                public void onComplete() { System.out.println("complete"); done.countDown(); }
            });
            for (int i = 1; i <= 3; i++) {
                Thread.sleep(100);
                System.out.println("  submit " + i);
                pub.submit(i);
            }
        }                                          // close() -> onComplete
        done.await();
    }
}
