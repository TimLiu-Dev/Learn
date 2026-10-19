public class Outlive {
    static class Counter {
        int count;
        Counter(int count) { this.count = count; }
    }

    static Counter makeCounter(int start) {
        Counter fresh = new Counter(start);  // object on the heap, reference in this frame
        return fresh;                        // the frame dies; the reference is handed back
    }

    public static void main(String[] args) {
        Counter kept = makeCounter(10);
        kept.count++;
        System.out.println(kept.count);
    }
}
