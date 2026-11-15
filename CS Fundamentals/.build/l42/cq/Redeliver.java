import java.util.*;

public class Redeliver {
    record Msg(int id, String body) {}

    public static void main(String[] args) {
        Deque<Msg> queue = new ArrayDeque<>(List.of(new Msg(1, "a"), new Msg(2, "b"), new Msg(3, "a")));
        Set<String> seen = new HashSet<>();
        List<String> sent = new ArrayList<>();
        boolean crashed = false;
        while (!queue.isEmpty()) {
            Msg m = queue.poll();
            if (!seen.add(m.body())) continue;          // skip anything already handled
            sent.add(m.body() + m.id());
            if (m.id() == 2 && !crashed) {              // crash before the ack:
                crashed = true;                         // the queue delivers #2 again
                queue.addLast(m);
            }
        }
        System.out.println(sent);
    }
}
