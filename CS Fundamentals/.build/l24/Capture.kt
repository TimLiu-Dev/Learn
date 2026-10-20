import java.lang.ref.WeakReference

// Which callbacks keep their Screen (and its 1 MB bitmap) alive?
class Screen {
    val bitmap = ByteArray(1_000_000)

    fun objectIgnoringScreen() = object : Runnable { override fun run() = println("tick") }
    fun objectUsingScreen() = object : Runnable { override fun run() = println(bitmap.size) }
    fun lambdaIgnoringScreen() = Runnable { println("tick") }
    fun lambdaUsingScreen() = Runnable { println(bitmap.size) }
}

// Keep only the callback, drop the Screen, request a GC: is the Screen gone?
fun check(name: String, make: (Screen) -> Runnable) {
    var screen: Screen? = Screen()
    val ref = WeakReference(screen)
    val callback = make(screen!!)
    screen = null
    System.gc()
    println(name + if (ref.get() == null) "collected" else "STILL ALIVE")
    callback.hashCode()                        // keeps callback reachable until here
}

fun main() {
    check("object, ignores screen: ", Screen::objectIgnoringScreen)
    check("object, uses bitmap:    ", Screen::objectUsingScreen)
    check("lambda, ignores screen: ", Screen::lambdaIgnoringScreen)
    check("lambda, uses bitmap:    ", Screen::lambdaUsingScreen)
}
