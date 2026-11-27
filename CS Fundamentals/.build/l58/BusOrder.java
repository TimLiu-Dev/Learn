import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BusOrder {
    // Two in-process event buses. Both deliver synchronously, on the publisher's thread.
    // They differ only in what happens when a handler publishes another event.
    interface Bus {
        void subscribe(Consumer<String> handler);
        void publish(String event);
    }

    static final class ImmediateBus implements Bus {
        private final List<Consumer<String>> handlers = new ArrayList<>();
        public void subscribe(Consumer<String> handler) { handlers.add(handler); }
        public void publish(String event) { handlers.forEach(h -> h.accept(event)); }   // nested publish runs right now
    }

    static final class QueuedBus implements Bus {
        private final List<Consumer<String>> handlers = new ArrayList<>();
        private final ArrayDeque<String> queue = new ArrayDeque<>();
        private boolean delivering = false;
        public void subscribe(Consumer<String> handler) { handlers.add(handler); }
        public void publish(String event) {
            queue.addLast(event);
            if (delivering) return;                      // finish the current event first
            delivering = true;
            try {
                while (!queue.isEmpty()) { String e = queue.removeFirst(); handlers.forEach(h -> h.accept(e)); }
            } finally { delivering = false; }
        }
    }

    static List<String> trace(Bus bus) {
        List<String> log = new ArrayList<>();
        // Lending: put a new copy on the shelf, then announce that it's ready to lend.
        bus.subscribe(e -> { if (e.equals("CopyAdded")) { log.add("lending saw " + e); bus.publish("CopyLendable"); } });
        // Notifications: tell members on the waiting list. It must hear about the copy before it's lendable.
        bus.subscribe(e -> log.add("notifications saw " + e));
        bus.publish("CopyAdded");
        return log;
    }

    public static void main(String[] args) {
        System.out.println("immediate: " + trace(new ImmediateBus()));
        System.out.println("queued:    " + trace(new QueuedBus()));
    }
}
