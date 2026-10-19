import java.util.List;

public class PQR {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    public static void main(String[] args) {
        Counter p = new Counter(1);
        Counter q = new Counter(1);
        Counter r = p;
        r.count += 10;
        List<Counter> all = List.of(p, q, r);
        all.get(1).count = 7;
        System.out.println(p.count + " " + q.count + " " + r.count);
        System.out.println(all.size());
        System.out.println(all.get(0).count + " " + all.get(2).count);
    }
}
