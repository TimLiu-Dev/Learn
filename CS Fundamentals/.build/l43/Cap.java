import java.util.*;

// Two replicas of one key-value store, joined by a network link we can cut.
// CP mode refuses writes it can't replicate; AP mode accepts them and syncs later.
public class Cap {
    static long clock = 0;                         // one logical clock, so timestamps are comparable

    record Versioned(String value, long ts, String node) {}

    static class Link { boolean up = true; }

    static class Node {
        final String name; final Link link; final String mode;
        Node peer;
        final Map<String, Versioned> data = new HashMap<>();
        final List<Map.Entry<String, Versioned>> unsent = new ArrayList<>();  // AP: writes the peer hasn't seen

        Node(String name, Link link, String mode) { this.name = name; this.link = link; this.mode = mode; }

        String write(String key, String value) {
            Versioned v = new Versioned(value, ++clock, name);
            if (mode.equals("CP")) {
                if (!link.up) return "REJECTED: can't reach " + peer.name + ", so the write can't be replicated";
                data.put(key, v); peer.data.put(key, v);
                return "OK (replicated to " + peer.name + ")";
            }
            data.put(key, v);
            if (link.up) peer.receive(key, v); else unsent.add(Map.entry(key, v));
            return link.up ? "OK (sent to " + peer.name + ")" : "OK locally (" + peer.name + " will hear later)";
        }

        String read(String key) { Versioned v = data.get(key); return v == null ? "-" : v.value(); }

        void receive(String key, Versioned incoming) {
            Versioned mine = data.get(key);
            if (mine == null || incoming.ts() > mine.ts()) {          // last write wins
                data.put(key, incoming);
            } else if (incoming.ts() < mine.ts()) {
                System.out.println("    " + name + " drops " + incoming.value() + " (t=" + incoming.ts() + " from "
                        + incoming.node() + "): older than " + mine.value() + " (t=" + mine.ts() + ")");
            }
        }

        void flush() { for (var e : unsent) peer.receive(e.getKey(), e.getValue()); unsent.clear(); }
    }

    static Node[] cluster(String mode, Link link) {
        Node a = new Node("A", link, mode), b = new Node("B", link, mode);
        a.peer = b; b.peer = a;
        return new Node[] { a, b };
    }

    public static void main(String[] args) {
        System.out.println("== CP mode: a bank balance");
        Link link = new Link();
        Node[] cp = cluster("CP", link);
        Node a = cp[0], b = cp[1];
        System.out.println("A write balance=100 -> " + a.write("balance", "100"));
        System.out.println("-- partition: the link between A and B is cut");
        link.up = false;
        System.out.println("A write balance=70  -> " + a.write("balance", "70"));
        System.out.println("B write balance=40  -> " + b.write("balance", "40"));
        System.out.println("reads: A=" + a.read("balance") + " B=" + b.read("balance") + "   (consistent, but no one can write)");

        System.out.println("\n== AP mode: a shopping cart");
        Link link2 = new Link();
        Node[] ap = cluster("AP", link2);
        Node c = ap[0], d = ap[1];
        System.out.println("A write cart=[book] -> " + c.write("cart", "[book]"));
        System.out.println("-- partition");
        link2.up = false;
        System.out.println("A write cart=[book,lamp] -> " + c.write("cart", "[book,lamp]"));
        System.out.println("B write cart=[book,pen]  -> " + d.write("cart", "[book,pen]"));
        System.out.println("reads: A=" + c.read("cart") + " B=" + d.read("cart") + "   (available, but they disagree)");
        System.out.println("-- heal: exchange the writes each side missed (last write wins)");
        link2.up = true;
        c.flush(); d.flush();
        System.out.println("reads: A=" + c.read("cart") + " B=" + d.read("cart") + "   (agree again, and the lamp is gone)");
    }
}
