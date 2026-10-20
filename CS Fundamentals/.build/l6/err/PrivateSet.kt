class Thermostat {
    var current: Int = 18
        private set
}

fun main() {
    val t = Thermostat()
    t.current = 50
}
