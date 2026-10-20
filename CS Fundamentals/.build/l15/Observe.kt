import kotlin.properties.Delegates

class Thermostat {
    var target: Int by Delegates.observable(20) { _, old, new ->     // runs AFTER each assignment
        println("  target $old -> $new")
    }
    var limit: Int by Delegates.vetoable(28) { _, _, new ->          // runs BEFORE; false = reject
        new in 15..30
    }
}

class Settings(map: Map<String, Any?>) {
    val theme: String by map                                         // reads map["theme"]
    val fontSize: Int by map                                         // reads map["fontSize"]
}

fun main() {
    val t = Thermostat()
    t.target = 22
    t.target = 19
    t.limit = 45
    println("limit after 45: ${t.limit}")
    t.limit = 25
    println("limit after 25: ${t.limit}")

    val s = Settings(mapOf("theme" to "dark", "fontSize" to 14))
    println("theme=${s.theme} fontSize=${s.fontSize}")
}
