package shop;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

// OBSERVER: a listener list; subscribe returns the way to unsubscribe
public final class OrderEvents {
    private final List<Consumer<OrderEvent>> listeners = new CopyOnWriteArrayList<>();

    public AutoCloseable subscribe(Consumer<OrderEvent> listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public void publish(OrderEvent e) {
        for (Consumer<OrderEvent> l : listeners) l.accept(e);
    }
}
