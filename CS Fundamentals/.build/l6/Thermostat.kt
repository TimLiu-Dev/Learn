class Thermostat(initialTarget: Int) {
    var target: Int = checked(initialTarget)    // a setter does NOT run for the initializer...
        set(value) {
            field = checked(value)              // ...so validate in both places
        }

    var current: Int = 18
        private set                             // readable everywhere, writable only in here

    val isHeating: Boolean
        get() = current < target                // computed on every read: no stored field

    fun tick() {
        if (current < target) current++ else if (current > target) current--
    }

    private fun checked(t: Int): Int {
        require(t in 5..30) { "target must be 5..30, was $t" }
        return t
    }
}

fun main() {
    val t = Thermostat(21)
    println("target=${t.target} current=${t.current} heating=${t.isHeating}")
    for (i in 1..3) t.tick()
    println("target=${t.target} current=${t.current} heating=${t.isHeating}")
    t.target = 19                               // looks like a field write; calls the setter
    println("target=${t.target} current=${t.current} heating=${t.isHeating}")
    try {
        t.target = 40
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
    println("target is still ${t.target}")
}
