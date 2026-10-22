package shop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RetryingNotifierTest {
    // A fake (L38): fails a set number of times, records every attempt, sends nothing
    static final class FlakyFake implements Notifier {
        private int failures;
        int attempts = 0;
        final List<String> delivered = new ArrayList<>();
        FlakyFake(int failures) { this.failures = failures; }
        public void send(String to, String message) {
            attempts++;
            if (failures-- > 0) throw new IllegalStateException("down");
            delivered.add(to + ":" + message);
        }
    }

    @Test void succeedsAfterTwoFailuresWithThreeAttempts() {
        FlakyFake fake = new FlakyFake(2);
        new RetryingNotifier(fake, 3).send("ada", "hi");
        assertEquals(3, fake.attempts);
        assertEquals(List.of("ada:hi"), fake.delivered);
    }

    @Test void givesUpAndRethrowsAfterTheLastAttempt() {
        FlakyFake fake = new FlakyFake(5);
        assertThrows(IllegalStateException.class, () -> new RetryingNotifier(fake, 3).send("ada", "hi"));
        assertEquals(3, fake.attempts);
    }

    @Test void factoryPicksTheNotifierFromConfig() {
        assertInstanceOf(EmailNotifier.class, Notifiers.from("email:x@test"));
        assertThrows(IllegalArgumentException.class, () -> Notifiers.from("pigeon"));
    }
}
