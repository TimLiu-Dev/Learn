import java.lang.ref.WeakReference;
import java.util.function.Function;

// Which callbacks keep their Screen (and its 1 MB bitmap) alive?
public class Capture {
    static class Screen {
        final byte[] bitmap = new byte[1_000_000];

        Runnable anonymousIgnoringScreen() {
            return new Runnable() { public void run() { System.out.println("tick"); } };
        }
        Runnable anonymousUsingScreen() {
            return new Runnable() { public void run() { System.out.println(bitmap.length); } };
        }
        Runnable lambdaIgnoringScreen() { return () -> System.out.println("tick"); }
        Runnable lambdaUsingScreen()    { return () -> System.out.println(bitmap.length); }
    }

    // Keep only the callback, drop the Screen, request a GC: is the Screen gone?
    static void check(String name, Function<Screen, Runnable> make) {
        Screen screen = new Screen();
        WeakReference<Screen> ref = new WeakReference<>(screen);
        Runnable callback = make.apply(screen);
        screen = null;
        System.gc();
        System.out.println(name + (ref.get() == null ? "collected" : "STILL ALIVE"));
        callback.hashCode();                   // keeps callback reachable until here
    }

    public static void main(String[] args) {
        check("anonymous, ignores screen: ", Screen::anonymousIgnoringScreen);
        check("anonymous, uses bitmap:    ", Screen::anonymousUsingScreen);
        check("lambda, ignores screen:    ", Screen::lambdaIgnoringScreen);
        check("lambda, uses bitmap:       ", Screen::lambdaUsingScreen);
    }
}
