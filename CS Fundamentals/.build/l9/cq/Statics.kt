import java.lang.reflect.Modifier

class Thermo {
    companion object {
        @JvmStatic fun indoor() = "indoor"
        fun outdoor() = "outdoor"
    }
}

fun main() {
    // The static methods Java can call directly on Thermo
    val statics = Thermo::class.java.declaredMethods
        .filter { Modifier.isStatic(it.modifiers) }
        .map { it.name }
        .sorted()
    println(statics)
}
