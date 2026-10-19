public class Cache {
    public static void main(String[] args) {
        Integer a = 127, b = 127;
        Integer c = 128, d = 128;
        System.out.println(a == b);        // same cached object
        System.out.println(c == d);        // two different objects
        System.out.println(c.equals(d));   // compares the values

        int plain = 128;
        System.out.println(c == plain);    // int on one side: unboxes, compares values

        Integer missing = null;
        System.out.println(missing == plain);  // unboxing null -> NullPointerException
    }
}
