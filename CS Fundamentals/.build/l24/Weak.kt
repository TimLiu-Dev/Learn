import java.lang.ref.WeakReference
import java.util.WeakHashMap

// Weak references don't keep their target alive; the GC may clear them.
class Screen(val name: String)

fun main() {
    val home = Screen("home")
    var settings: Screen? = Screen("settings")

    val lastScroll = WeakHashMap<Screen, String>()        // keys held weakly
    lastScroll[home] = "row 12"
    lastScroll[settings!!] = "row 3"
    val ref = WeakReference(settings)

    println("before: entries=${lastScroll.size} ref=${ref.get()?.name}")
    settings = null                  // the only strong reference is gone
    System.gc()                      // a request, not a guarantee
    println("after:  entries=${lastScroll.size} ref=${ref.get()?.name}")
    Thread.sleep(100)                // give the JVM time to queue the cleared entry
    println("later:  entries=${lastScroll.size}")
    println("home still there: ${lastScroll[home]}")
}
