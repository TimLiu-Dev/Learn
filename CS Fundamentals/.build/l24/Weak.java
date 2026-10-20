import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

// Weak references don't keep their target alive; the GC may clear them.
public class Weak {
    static class Screen {
        final String name;
        Screen(String name) { this.name = name; }
    }

    public static void main(String[] args) throws InterruptedException {
        Screen home = new Screen("home");
        Screen settings = new Screen("settings");

        Map<Screen, String> lastScroll = new WeakHashMap<>(); // keys held weakly
        lastScroll.put(home, "row 12");
        lastScroll.put(settings, "row 3");
        WeakReference<Screen> ref = new WeakReference<>(settings);

        System.out.println("before: entries=" + lastScroll.size() + " ref=" + ref.get().name);
        settings = null;                 // the only strong reference is gone
        System.gc();                     // a request, not a guarantee
        System.out.println("after:  entries=" + lastScroll.size() + " ref=" + ref.get());
        Thread.sleep(100);               // give the JVM time to queue the cleared entry
        System.out.println("later:  entries=" + lastScroll.size());
        System.out.println("home still there: " + lastScroll.get(home));
    }
}
