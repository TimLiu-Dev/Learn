import kotlin.math.abs

val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

// % would give -1 for an offset of -1; mod() always lands in 0..6
fun weekday(offset: Int): String = days[offset.mod(7)]

// Convert before adding: no Int overflow, and no integer division
fun average(a: Int, b: Int): Double = (a.toDouble() + b) / 2

fun closeEnough(x: Double, y: Double): Boolean = abs(x - y) < 1e-9
