import kotlin.math.abs

val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

fun weekday(offset: Int): String = days[offset.mod(7)]

// Fixes the lost half, but a + b is still Int arithmetic
fun average(a: Int, b: Int): Double = (a + b) / 2.0

fun closeEnough(x: Double, y: Double): Boolean = abs(x - y) < 1e-9
