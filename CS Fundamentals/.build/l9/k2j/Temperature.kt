@file:JvmName("Temperatures")      // Java sees this file's top-level code as class Temperatures

import java.io.IOException

const val ABSOLUTE_ZERO_C = -273.15                     // a real static constant for Java

fun celsiusToFahrenheit(c: Double): Double = c * 9 / 5 + 32

class Thermometer(val location: String, var isOnline: Boolean = true) {
    var readings = 0
        private set

    @JvmField val unit = "C"                            // a plain public field, no getter

    @JvmOverloads                                       // generate the shorter Java overloads
    fun record(celsius: Double, note: String = "", rounded: Boolean = false): String {
        readings++
        val value = if (rounded) Math.round(celsius).toDouble() else celsius
        return if (note.isEmpty()) "$location: $value$unit" else "$location: $value$unit ($note)"
    }

    @Throws(IOException::class)                         // declare it, so Java may catch it
    fun calibrate(file: String) {
        throw IOException("calibration file missing: $file")
    }

    companion object {
        const val MAX_READINGS = 1000
        @JvmStatic fun indoor(): Thermometer = Thermometer("indoor")   // real static method
        fun outdoor(): Thermometer = Thermometer("outdoor")            // only via Companion
    }
}

object Registry {                                       // a singleton
    private val names = mutableListOf<String>()
    fun register(name: String) { names.add(name) }
    val count: Int get() = names.size
}
