import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// A long-lived event bus keeps every subscriber reachable until it unsubscribes.
public class Listeners {
    static class EventBus {
        private final List<Consumer<String>> listeners = new ArrayList<>();
        void subscribe(Consumer<String> l)   { listeners.add(l); }
        void unsubscribe(Consumer<String> l) { listeners.remove(l); }
        int size() { return listeners.size(); }
    }

    static final EventBus BUS = new EventBus();      // lives as long as the app
    static boolean fixed;

    static class Screen {
        private final byte[] bitmap = new byte[1_000_000];      // 1 MB of pixels
        private final Consumer<String> listener = this::onEvent; // captures this
        Screen() { BUS.subscribe(listener); }
        void onEvent(String e) { System.out.println(e + " " + bitmap.length); }
        void close() {
            if (fixed) BUS.unsubscribe(listener);    // the line people forget
        }
    }

    public static void main(String[] args) {
        fixed = args.length > 0;
        for (int i = 1; i <= 500; i++) {
            Screen s = new Screen();
            s.close();                               // the user left the screen
            if (i % 20 == 0) System.out.println("opened " + i + ", listeners " + BUS.size());
        }
    }
}
