package platform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// An in-process event bus. Delivery is synchronous, on the publisher's thread, and in publish
// order: an event published by a handler waits in the queue until the current one is done.
// Not thread-safe; one bus per single-threaded application (L26 would add a lock).
public final class EventBus {
    private record Subscription(Class<?> type, Consumer<Object> handler) {}

    private final List<Subscription> subscriptions = new ArrayList<>();
    private final ArrayDeque<Object> queue = new ArrayDeque<>();
    private boolean delivering = false;

    public <E> void subscribe(Class<E> type, Consumer<? super E> handler) {
        subscriptions.add(new Subscription(type, event -> handler.accept(type.cast(event))));
    }

    public void publish(Object event) {
        queue.addLast(event);
        if (delivering) return;                // a handler published: deliver it after the current event
        delivering = true;
        try {
            while (!queue.isEmpty()) {
                Object next = queue.removeFirst();
                for (Subscription s : List.copyOf(subscriptions)) {
                    if (s.type().isInstance(next)) s.handler().accept(next);
                }
            }
        } finally {
            delivering = false;
            queue.clear();                     // a handler threw: don't deliver leftovers later, out of context
        }
    }
}
