import java.util.ArrayList;
import java.util.List;

public class Reset {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    static void resetAll(List<Counter> counters) {
        for (Counter c : counters) c.count = 0;   // mutates the shared objects: caller sees it
    }

    static void replaceList(List<Counter> counters) {
        counters = new ArrayList<>();             // re-points the parameter copy only
        counters.add(new Counter(42));
    }

    public static void main(String[] args) {
        List<Counter> counters = new ArrayList<>(List.of(new Counter(3), new Counter(7)));
        resetAll(counters);
        System.out.println(counters.get(0).count + " " + counters.get(1).count);
        replaceList(counters);
        System.out.println(counters.size() + " " + counters.get(0).count);
    }
}
