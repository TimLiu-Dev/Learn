import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWrite {
    // A phone book that is searched often and changed rarely.
    static class Directory {
        private final Lock readLock, writeLock;
        private final Map<String, String> numbers = new HashMap<>(Map.of("ana", "555-0101", "bo", "555-0102"));
        Directory(Lock readLock, Lock writeLock) { this.readLock = readLock; this.writeLock = writeLock; }
        String lookup(String name) throws InterruptedException {
            readLock.lock();
            try { Thread.sleep(50); return numbers.get(name); }    // the sleep stands in for a slow search
            finally { readLock.unlock(); }
        }
        void update(String name, String number) {
            writeLock.lock();
            try { numbers.put(name, number); } finally { writeLock.unlock(); }
        }
    }

    static long fourReaders(Directory d) throws InterruptedException {
        long start = System.nanoTime();
        Thread[] readers = new Thread[4];
        for (int i = 0; i < 4; i++) {
            readers[i] = new Thread(() -> {
                try { d.lookup("ana"); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            readers[i].start();
        }
        for (Thread t : readers) t.join();
        return (System.nanoTime() - start) / 1_000_000;
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock mutex = new ReentrantLock();                // one lock for both roles
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        Directory plain = new Directory(mutex, mutex);
        Directory shared = new Directory(rw.readLock(), rw.writeLock());
        for (int round = 0; round < 2; round++) {                 // round 1 is warm-up
            System.out.println("4 lookups: ReentrantLock " + fourReaders(plain)
                + " ms, read-write lock " + fourReaders(shared) + " ms");
        }
        shared.update("bo", "555-0199");
        System.out.println("after update: bo = " + shared.lookup("bo"));

        rw.readLock().lock();
        try {
            System.out.println("holding read lock, writeLock().tryLock() = " + rw.writeLock().tryLock());
        } finally { rw.readLock().unlock(); }
        rw.writeLock().lock();
        try {
            System.out.println("holding write lock, readLock().tryLock() = " + rw.readLock().tryLock());
            rw.readLock().unlock();
        } finally { rw.writeLock().unlock(); }
    }
}
