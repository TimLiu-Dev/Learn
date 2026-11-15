import java.util.*;
import java.util.function.Consumer;

public class Unsubscribe {
    record Screen(String name) implements Consumer<String> {
        public void accept(String event) { System.out.println(name + ": " + event); }
    }

    public static void main(String[] args) {
        Screen a = new Screen("desk");
        Screen b = new Screen("desk");
        List<Consumer<String>> listeners = new ArrayList<>(List.of(a, b));
        listeners.remove(b);                                  // unsubscribe b
        System.out.println(listeners.size() + " " + (listeners.get(0) == b));
    }
}
