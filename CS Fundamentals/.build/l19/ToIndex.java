public class ToIndex {
    public static void main(String[] args) {
        String[] buckets = new String[16];
        int h = "banana".hashCode();

        System.out.println("h                = " + h);
        System.out.println("h % 16           = " + (h % 16));             // keeps the sign of h
        System.out.println("floorMod(h, 16)  = " + Math.floorMod(h, 16));  // 0..15, always
        System.out.println("h & 15           = " + (h & 15));             // powers of two only
        System.out.println("spread           = " + ((h ^ (h >>> 16)) & 15));

        buckets[Math.floorMod(h, 16)] = "banana";   // fine
        buckets[h % 16] = "banana";                 // index -11
    }
}
