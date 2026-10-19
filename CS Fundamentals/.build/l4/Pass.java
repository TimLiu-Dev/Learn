public class Pass {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    static void bumpInt(int n) {
        n++;                     // changes the copy only
    }

    static void bump(Counter c) {
        c.count++;               // follows the copied arrow to the SAME object
    }

    static void replace(Counter c) {
        c = new Counter(100);    // re-points the copy only
    }

    static void swap(int a, int b) {
        int tmp = a;
        a = b;
        b = tmp;                 // swaps the copies; the caller sees nothing
    }

    public static void main(String[] args) {
        int n = 5;
        bumpInt(n);
        System.out.println("n=" + n);

        Counter counter = new Counter(5);
        bump(counter);
        System.out.println("after bump: " + counter.count);
        replace(counter);
        System.out.println("after replace: " + counter.count);

        int p = 1, q = 2;
        swap(p, q);
        System.out.println("p=" + p + " q=" + q);
    }
}
