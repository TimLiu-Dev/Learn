// A one-slot mailbox built on the monitor's own wait set: the pre-java.util.concurrent way.
public class Mailbox {
    private String letter;                         // null means empty; guarded by this

    synchronized void put(String s) throws InterruptedException {
        while (letter != null) wait();             // full: release the monitor and sleep
        letter = s;
        notifyAll();                               // wake everyone; each re-checks its own condition
    }

    synchronized String take() throws InterruptedException {
        while (letter == null) wait();             // a while, never an if
        String s = letter;
        letter = null;
        notifyAll();
        return s;
    }

    public static void main(String[] args) throws InterruptedException {
        Mailbox box = new Mailbox();
        Thread postman = new Thread(() -> {
            try { for (int i = 1; i <= 3; i++) box.put("letter " + i); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        postman.start();
        for (int i = 0; i < 3; i++) System.out.println("took " + box.take());
        postman.join();
    }
}
