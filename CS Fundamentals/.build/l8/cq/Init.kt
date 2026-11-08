open class Base {
    init {
        println("Base sees " + describe())
    }

    open fun describe() = "base"
}

class Derived : Base() {
    val label = "derived"
    override fun describe() = "label=$label"
}

fun main() {
    Derived()
}
