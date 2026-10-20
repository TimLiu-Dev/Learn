public class AnyBox {
    Object item;                                 // holds anything at all
    AnyBox(Object item) { this.item = item; }

    public static void main(String[] args) {
        AnyBox box = new AnyBox("hello");
        String s = (String) box.item;            // we "know" it's a String, so cast
        System.out.println(s.toUpperCase());

        box.item = 42;                           // some other code puts in an Integer...
        String t = (String) box.item;            // ...and the cast fails at RUNTIME
        System.out.println(t.toUpperCase());
    }
}
