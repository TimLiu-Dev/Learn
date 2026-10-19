public class Alias {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    public static void main(String[] args) {
        int x = 1;
        int y = x;          // copies the number
        y = 99;
        System.out.println("x=" + x + " y=" + y);

        Counter a = new Counter(1);
        Counter b = a;      // copies the reference: one object, two arrows
        b.count = 99;
        System.out.println("a.count=" + a.count + " b.count=" + b.count);

        Counter c = a;
        c = new Counter(5); // re-points c; a and the old object are untouched
        System.out.println("a.count=" + a.count + " c.count=" + c.count);
    }
}
