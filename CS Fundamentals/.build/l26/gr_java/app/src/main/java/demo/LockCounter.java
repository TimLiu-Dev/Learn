package demo;

import java.util.concurrent.locks.ReentrantLock;

public class LockCounter implements Counter {         // fix 2: an explicit lock
    private final ReentrantLock lock = new ReentrantLock();
    private int count;

    @Override public void increment() {
        lock.lock();
        try { count++; } finally { lock.unlock(); }
    }

    @Override public int value() {
        lock.lock();
        try { return count; } finally { lock.unlock(); }
    }
}
