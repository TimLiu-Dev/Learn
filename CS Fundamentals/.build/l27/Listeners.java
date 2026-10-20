// A listener that unregisters itself while the bus is notifying everyone.
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Listeners {
    static void fire(List<Consumer<String>> listeners) {
        Consumer<String> oneShot = new Consumer<>() {
            public void accept(String e) {
                System.out.println("  one-shot got " + e + ", unregistering");
                listeners.remove(this);              // modifies the list mid-iteration
            }
        };
        listeners.add(oneShot);
        listeners.add(e -> System.out.println("  logger got " + e));
        for (Consumer<String> l : listeners) l.accept("saved");
        System.out.println("  listeners left: " + listeners.size());
    }

    public static void main(String[] args) {
        System.out.println("CopyOnWriteArrayList:");
        fire(new CopyOnWriteArrayList<>());
        System.out.println("ArrayList:");
        fire(new ArrayList<>());
    }
}
