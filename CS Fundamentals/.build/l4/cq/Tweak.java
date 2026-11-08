public class Tweak {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    static void tweak(Counter c) {
        c.count = 2;
        c = new Counter(3);
        c.count = 4;
    }

    public static void main(String[] args) {
        Counter k = new Counter(1);
        tweak(k);
        System.out.println(k.count);
    }
}
