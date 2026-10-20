public class RangeEdge {
    public static void main(String[] args) {
        int count = 0;
        for (int i = Integer.MAX_VALUE - 2; i <= Integer.MAX_VALUE; i++) {
            if (++count == 10) break;      // safety valve: this loop never ends on its own
        }
        System.out.println("Java loop ran " + count + " times (stopped by the safety valve)");
    }
}
