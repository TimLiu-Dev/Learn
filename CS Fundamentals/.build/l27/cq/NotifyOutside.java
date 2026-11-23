public class NotifyOutside {
    static String tryNotify(Object lock) {
        try {
            lock.notifyAll();
            return "ok";
        } catch (IllegalMonitorStateException e) {
            return e.getClass().getSimpleName();
        }
    }

    public static void main(String[] args) {
        Object lock = new Object();
        String outside = tryNotify(lock);
        String inside;
        synchronized (lock) { inside = tryNotify(lock); }
        System.out.println("outside: " + outside + ", inside: " + inside);
    }
}
