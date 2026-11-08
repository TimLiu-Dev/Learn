import java.util.ArrayList;
import java.util.List;

// Exercise: a group chat that never shows a reply before the message it answers.
// Each message carries a vector clock; a member holds a message back until everything
// it depends on has been delivered (causal delivery).
public class CausalChat {
    record Msg(int sender, String text, int[] vc) {}

    static final List<String> NAMES = List.of("Ana", "Bob", "Cleo");

    static class Member {
        final int id;
        private final int[] delivered;                // delivered[k] = how many of k's messages I've shown
        private final List<Msg> waiting = new ArrayList<>();
        final List<String> screen = new ArrayList<>();
        Member(int id, int n) { this.id = id; this.delivered = new int[n]; }

        Msg send(String text) {
            int[] vc = delivered.clone();
            vc[id]++;
            Msg m = new Msg(id, text, vc);
            show(m);
            return m;
        }

        void receive(Msg m) {
            waiting.add(m);
            while (true) {
                Msg next = waiting.stream().filter(this::ready).findFirst().orElse(null);
                if (next == null) break;
                waiting.remove(next);
                show(next);
            }
            for (Msg w : waiting)
                System.out.println("  " + NAMES.get(id) + " holds back " + NAMES.get(w.sender()) + ": \"" + w.text() + "\" (missing an earlier message)");
        }

        private boolean ready(Msg m) {
            if (m.vc()[m.sender()] != delivered[m.sender()] + 1) return false;
            for (int k = 0; k < delivered.length; k++)
                if (k != m.sender() && m.vc()[k] > delivered[k]) return false;
            return true;
        }

        private void show(Msg m) {
            delivered[m.sender()]++;
            screen.add(NAMES.get(m.sender()) + ": " + m.text());
        }
    }

    public static void main(String[] args) {
        Member ana = new Member(0, 3), bob = new Member(1, 3), cleo = new Member(2, 3);
        Msg m1 = ana.send("Dinner at 7?");
        bob.receive(m1);
        Msg m2 = bob.send("Yes, 7 works");
        Msg m3 = cleo.send("I'm running late today");   // Cleo hasn't seen anything yet: concurrent
        // The network delivers in a bad order:
        cleo.receive(m2);                               // the reply overtakes the question
        ana.receive(m3); ana.receive(m2);
        cleo.receive(m1);
        bob.receive(m3);
        for (Member m : List.of(ana, bob, cleo)) {
            System.out.println(NAMES.get(m.id) + "'s screen:");
            m.screen.forEach(s -> System.out.println("    " + s));
        }
    }
}
