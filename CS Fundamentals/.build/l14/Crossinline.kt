// The lambda runs later, inside another object, so it can't `return` from the caller
inline fun later(crossinline task: () -> Unit): Runnable = Runnable { task() }

// Inline the cheap message lambda, but keep `onDone` as a real object we can store
val callbacks = mutableListOf<() -> Unit>()
inline fun log(message: () -> String, noinline onDone: () -> Unit) {
    println(message())
    callbacks.add(onDone)
}

fun main() {
    val r = later { println("ran later") }
    log({ "saving" }) { println("saved callback") }
    r.run()
    callbacks.forEach { it() }
}
