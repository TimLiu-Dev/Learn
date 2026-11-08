import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Tracked<T>(initial: T) : ReadWriteProperty<Draft, T> {
    private var value = initial

    override fun getValue(thisRef: Draft, property: KProperty<*>): T = value

    override fun setValue(thisRef: Draft, property: KProperty<*>, value: T) {
        this.value = value
        if (value != this.value) thisRef.dirty += property.name
    }
}

class Draft {
    val dirty = mutableSetOf<String>()
    var title: String by Tracked("")
    var body: String by Tracked("")
    var words: Int by Tracked(0)

    fun save(): String {
        val message = if (dirty.isEmpty()) "nothing to save" else "saved " + dirty.sorted().joinToString(", ")
        dirty.clear()
        return message
    }
}
