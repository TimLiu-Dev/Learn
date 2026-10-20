package demo;

public class UnsafeCounter implements Counter {
    private int count;
    @Override public void increment() { count++; }
    @Override public int value() { return count; }
}
