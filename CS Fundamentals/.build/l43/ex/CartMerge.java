import java.util.*;

// Exercise solution: keep both versions of a concurrent write, then merge them.
// A version remembers which version it replaced (its parent). Two versions with
// the same parent were written without seeing each other: a real conflict.
public class CartMerge {
    static long clock = 0;

    record Cart(Set<String> items, long ts, long parent, String node) {}

    static class Replica {
        final String name;
        Cart cart = new Cart(Set.of(), 0, 0, "-");
        final Deque<Cart> unsent = new ArrayDeque<>();
        Replica(String name) { this.name = name; }

        void add(String item) {
            Set<String> items = new TreeSet<>(cart.items()); items.add(item);
            cart = new Cart(items, ++clock, cart.ts(), name);
            unsent.add(cart);
        }

        void receive(Cart incoming) {
            if (incoming.parent() == cart.ts()) {
                cart = incoming;                                                   // it built on mine: take it
            } else if (incoming.parent() == cart.parent() && incoming.ts() != cart.ts()) {
                Set<String> merged = new TreeSet<>(cart.items()); merged.addAll(incoming.items());  // siblings: merge
                System.out.println("    " + name + ": conflict " + cart.items() + " vs " + incoming.items() + " -> keep both: " + merged);
                cart = new Cart(merged, ++clock, Math.max(cart.ts(), incoming.ts()), name);
            }                                                                      // else: already have it or newer
        }
    }

    public static void main(String[] args) {
        Replica a = new Replica("A"), b = new Replica("B");
        a.add("book"); b.receive(a.unsent.removeFirst());
        System.out.println("synced: A=" + a.cart.items() + " B=" + b.cart.items());
        System.out.println("-- partition");
        a.add("lamp"); b.add("pen");
        System.out.println("diverged: A=" + a.cart.items() + " B=" + b.cart.items());
        System.out.println("-- heal");
        b.receive(a.unsent.removeFirst()); a.receive(b.unsent.removeFirst());
        System.out.println("A=" + a.cart.items() + " B=" + b.cart.items());
    }
}
