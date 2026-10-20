import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Trimmed : ReadWriteProperty<Any?, String> {
    private var value = ""
    override fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value.trim()
    }
}

class Profile {
    var name: String by Trimmed()
    var city: String by Trimmed()
    val initials: String by lazy {
        println("  (computing initials)")
        name.split(" ").joinToString("") { it.take(1) }
    }
}

interface Storage {
    fun save(key: String, value: String)
    fun load(key: String): String?
    fun keys(): Set<String>
}

class MemoryStorage : Storage {
    private val data = mutableMapOf<String, String>()
    override fun save(key: String, value: String) { data[key] = value }
    override fun load(key: String): String? = data[key]
    override fun keys(): Set<String> = data.keys
}

class LoggingStorage(private val inner: Storage) : Storage by inner {   // forwards load() and keys()
    override fun save(key: String, value: String) {
        println("  save $key")
        inner.save(key, value)
    }
}

fun main() {
    val p = Profile()
    p.name = "  Ada Lovelace "
    p.city = " London"
    println("[${p.name}] [${p.city}]")
    println("initials: ${p.initials}")
    println("initials again: ${p.initials}")

    val store: Storage = LoggingStorage(MemoryStorage())
    store.save("theme", "dark")
    store.save("lang", "en")
    println("theme=${store.load("theme")} keys=${store.keys()}")
}
