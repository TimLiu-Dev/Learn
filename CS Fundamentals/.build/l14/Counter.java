import java.util.function.IntSupplier;

public class Counter {
    static IntSupplier makeCounter() {
        int[] count = {0};             // must be effectively final, so box it
        return () -> ++count[0];       // the lambda captures the array
    }

    public static void main(String[] args) {
        IntSupplier a = makeCounter();
        IntSupplier b = makeCounter();
        a.getAsInt();
        a.getAsInt();
        System.out.println(a.getAsInt());
        System.out.println(b.getAsInt());
    }
}
