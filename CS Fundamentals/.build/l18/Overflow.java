public class Overflow {
    // A pretend sorted array of 2 billion elements: element i is 2 * i.
    // Too big to allocate (8 GB of longs), but the index arithmetic is identical.
    record Huge(int size) {
        long get(int i) {
            if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index " + i + " out of bounds for length " + size);
            return 2L * i;
        }
    }

    static int searchBroken(Huge a, long target) {
        int lo = 0, hi = a.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;                 // the textbook line
            if (a.get(mid) < target) lo = mid + 1; else if (a.get(mid) > target) hi = mid - 1; else return mid;
        }
        return -1;
    }

    static int searchFixed(Huge a, long target) {
        int lo = 0, hi = a.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;            // hi - lo can't overflow
            if (a.get(mid) < target) lo = mid + 1; else if (a.get(mid) > target) hi = mid - 1; else return mid;
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(1_500_000_000 + 1_999_999_999);           // int overflow (L2)
        System.out.println((1_500_000_000 + 1_999_999_999) >>> 1);   // the JDK's fix: unsigned shift
        Huge a = new Huge(2_000_000_000);
        System.out.println("fixed:  " + searchFixed(a, 3_800_000_000L));
        System.out.println("broken: " + searchBroken(a, 3_800_000_000L));
    }
}
