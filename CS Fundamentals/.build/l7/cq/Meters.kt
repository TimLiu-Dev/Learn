@JvmInline
value class Meters(val value: Double)

fun main() {
    val a = Meters(3.0)
    val b: Any = Meters(3.0)
    println("${a == Meters(3.0)} ${b == a} ${b.javaClass.simpleName}")
}
