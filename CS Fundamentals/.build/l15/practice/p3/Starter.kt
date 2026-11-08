import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Tracked<T>(initial: T) : ReadWriteProperty<Draft, T> {
    override fun getValue(thisRef: Draft, property: KProperty<*>): T = TODO("return the stored value")

    override fun setValue(thisRef: Draft, property: KProperty<*>, value: T) {
        TODO("store it; if it changed, add the property's name to thisRef.dirty")
    }
}

class Draft {
    val dirty = mutableSetOf<String>()
    var title: String = ""          // attach Tracked to these three
    var body: String = ""
    var words: Int = 0

    fun save(): String = TODO("\"saved a, b\" with sorted names, or \"nothing to save\"; then clear dirty")
}
