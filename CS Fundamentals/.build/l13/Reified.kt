import kotlin.reflect.KClass

// 1. The Java way also works in Kotlin: pass a class token
fun <T : Any> only(items: List<Any>, type: KClass<T>): List<T> {
    val out = mutableListOf<T>()
    for (x in items) if (type.isInstance(x)) out.add(type.java.cast(x))
    return out
}

// 2. The Kotlin way: inline + reified keeps T at the call site
inline fun <reified T> List<Any>.only(): List<T> {
    val out = mutableListOf<T>()
    for (x in this) if (x is T) out.add(x)       // "is T" is legal here
    return out
}

inline fun <reified T> typeName(): String = T::class.simpleName ?: "?"

fun main() {
    val mixed: List<Any> = listOf("a", 1, "b", 2.5, 3)
    println(only(mixed, String::class))           // token
    println(mixed.only<Int>())                    // reified
    println(mixed.filterIsInstance<Double>())     // the stdlib does the same thing
    println(typeName<StringBuilder>())
}
