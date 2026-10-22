import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Events {
    record LoanEvent(String kind, String title) {}

    interface LoanListener { void onEvent(LoanEvent e); }

    // The subject: it knows its listeners only through the interface
    static class LoanDesk {
        // Copy-on-write: a listener may unsubscribe while we are notifying (L27)
        private final List<LoanListener> listeners = new CopyOnWriteArrayList<>();

        // Return a handle, so callers can't lose the way to unsubscribe (L24's leak)
        AutoCloseable subscribe(LoanListener l) {
            listeners.add(l);
            return () -> listeners.remove(l);
        }
        void lend(String title)     { publish(new LoanEvent("lent", title)); }
        void giveBack(String title) { publish(new LoanEvent("returned", title)); }
        private void publish(LoanEvent e) { for (LoanListener l : listeners) l.onEvent(e); }
        int listenerCount() { return listeners.size(); }
    }

    public static void main(String[] args) throws Exception {
        LoanDesk desk = new LoanDesk();
        List<String> audit = new ArrayList<>();
        desk.subscribe(e -> audit.add(e.kind() + ":" + e.title()));
        try (AutoCloseable screen = desk.subscribe(e -> System.out.println("screen: " + e.title() + " " + e.kind()))) {
            desk.lend("Dune");
            desk.giveBack("Dune");
            System.out.println("listeners while screen open: " + desk.listenerCount());
        }   // closing the screen unsubscribes it
        desk.lend("Emma");
        System.out.println("listeners after close: " + desk.listenerCount());
        System.out.println("audit: " + audit);

        // The JDK's ready-made observer helper for one property
        PropertyChangeSupport fee = new PropertyChangeSupport(desk);
        fee.addPropertyChangeListener("fee", ev -> System.out.println("fee " + ev.getOldValue() + " -> " + ev.getNewValue()));
        fee.firePropertyChange("fee", 0, 250);
        fee.firePropertyChange("fee", 250, 250);   // equal values: no event
    }
}
