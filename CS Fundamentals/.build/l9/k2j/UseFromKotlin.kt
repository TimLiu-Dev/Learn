fun main() {
    println(celsiusToFahrenheit(100.0))
    println(ABSOLUTE_ZERO_C)

    val t = Thermometer.indoor()
    val out = Thermometer.outdoor()
    println("${t.location} online=${t.isOnline}")
    out.isOnline = false
    println("${out.location} online=${out.isOnline}")

    println(t.record(21.46))
    println(t.record(21.46, note = "after lunch"))
    println(t.record(21.46, "rounded", rounded = true))
    println("readings=${t.readings} unit=${t.unit} max=${Thermometer.MAX_READINGS}")

    Registry.register("indoor")
    println("registered=${Registry.count}")

    try {
        t.calibrate("cal.txt")
    } catch (e: java.io.IOException) {
        println("caught: ${e.message}")
    }
}
