import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// The bus holds listeners weakly, so a forgotten unsubscribe no longer leaks.
public class WeakBus {
    static class EventBus {
        private final List<WeakReference<Consumer<String>>> listeners = new ArrayList<>();
        void subscribe(Consumer<String> l) { listeners.add(new WeakReference<>(l)); }
        int publish(String event) {                    // returns how many listeners got it
            listeners.removeIf(ref -> ref.get() == null);  // drop cleared entries
            for (WeakReference<Consumer<String>> ref : listeners) {
                Consumer<String> l = ref.get();
                if (l != null) l.accept(event);
            }
            return listeners.size();
        }
    }

    static final EventBus BUS = new EventBus();

    static class Screen {
        private final byte[] bitmap = new byte[1_000_000];
        private final Consumer<String> listener = this::onEvent; // the Screen keeps its listener alive
        Screen() { BUS.subscribe(listener); }
        void onEvent(String e) { if (bitmap.length == 0) System.out.println(e); }
    }

    public static void main(String[] args) {
        Screen visible = new Screen();                 // one screen the user is looking at
        for (int i = 1; i <= 500; i++) {
            new Screen();                              // opened and left: never unsubscribed
            if (i % 100 == 0) System.out.println("opened " + i + ", delivered to " + BUS.publish("tick"));
        }
        System.gc();
        System.out.println("after GC, delivered to " + BUS.publish("tick"));
        System.out.println("visible screen still open: " + (visible.bitmap.length > 0));
    }
}
