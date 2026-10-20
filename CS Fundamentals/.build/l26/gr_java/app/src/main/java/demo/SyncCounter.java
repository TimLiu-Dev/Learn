package demo;

public class SyncCounter implements Counter {         // fix 1: the object's own monitor
    private int count;
    @Override public synchronized void increment() { count++; }
    @Override public synchronized int value() { return count; }
}
