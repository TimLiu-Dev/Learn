class Temperature(celsius: Double) {
    var celsius: Double = check(celsius)
        set(value) {
            field = check(value)
        }

    var fahrenheit: Double                      // no backing field: stored as celsius
        get() = celsius * 9 / 5 + 32
        set(value) {
            celsius = (value - 32) * 5 / 9      // goes through celsius's setter
        }

    private fun check(c: Double): Double {
        require(c >= -273.15) { "below absolute zero: $c" }
        return c
    }
}

fun main() {
    val t = Temperature(100.0)
    println("${t.celsius}C = ${t.fahrenheit}F")
    t.fahrenheit = 32.0
    println("${t.celsius}C = ${t.fahrenheit}F")
    try {
        t.celsius = -300.0
    } catch (e: IllegalArgumentException) {
        println("rejected: ${e.message}")
    }
}
