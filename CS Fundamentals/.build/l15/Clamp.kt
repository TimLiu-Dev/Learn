import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Clamped(private val range: IntRange, initial: Int) : ReadWriteProperty<Any?, Int> {
    private var value = initial.coerceIn(range)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        val clamped = value.coerceIn(range)
        if (clamped != value) println("  ${property.name}: $value clamped to $clamped")
        this.value = clamped
    }
}

class Speaker {
    var volume: Int by Clamped(0..10, 5)          // one line per property, logic written once
    var balance: Int by Clamped(-5..5, 0)
}

fun main() {
    val s = Speaker()
    s.volume = 7
    s.volume = 42
    s.balance = -9
    println("volume=${s.volume} balance=${s.balance}")
}
