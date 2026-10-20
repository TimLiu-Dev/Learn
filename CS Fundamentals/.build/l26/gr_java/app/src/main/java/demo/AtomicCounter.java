package demo;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter implements Counter {       // fix 3: lock-free compare-and-set
    private final AtomicInteger count = new AtomicInteger();
    @Override public void increment() { count.incrementAndGet(); }
    @Override public int value() { return count.get(); }
}
