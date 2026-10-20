package demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NaiveCounterTest {
    @Test
    void twoThreadsTimes1000IncrementsGive2000() throws InterruptedException {   // too small, no start gate
        Counter c = new UnsafeCounter();
        Thread a = new Thread(() -> { for (int i = 0; i < 1_000; i++) c.increment(); });
        Thread b = new Thread(() -> { for (int i = 0; i < 1_000; i++) c.increment(); });
        a.start(); b.start();
        a.join(); b.join();
        assertEquals(2_000, c.value());
    }
}
