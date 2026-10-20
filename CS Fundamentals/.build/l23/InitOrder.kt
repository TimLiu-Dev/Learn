object Registry {
    init { println("  [Registry initialised]") }
    val items = listOf("a", "b")
}

class Settings {
    companion object {
        init { println("  [Settings.Companion initialised]") }
        fun theme() = "dark"
    }
}

fun main() {
    println("main starts")
    println("Registry: ${Registry.items}")
    println("Settings: ${Settings.theme()}")
    println("Registry again: ${Registry.items}")
}
