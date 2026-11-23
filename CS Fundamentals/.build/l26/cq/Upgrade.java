import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Upgrade {
    public static void main(String[] args) {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        rw.readLock().lock();
        rw.readLock().lock();
        System.out.print(rw.getReadLockCount() + " ");
        System.out.print(rw.writeLock().tryLock() + " ");
        rw.readLock().unlock();
        rw.readLock().unlock();
        System.out.print(rw.writeLock().tryLock() + " ");
        System.out.println(rw.readLock().tryLock());
    }
}
