class Holder<T>(capacity: Int) {
    private val data = arrayOfNulls<T>(capacity)     // wants a real T at runtime
}

fun main() = println(Holder<String>(4))
