open class Animal {
    open fun sound(): String = "..."
}

class Dog : Animal()
class Cat : Animal()
class Cow : Animal()

// A chain of type checks: every new animal means editing this function.
fun soundOf(a: Animal): String = when (a) {
    is Dog -> "Woof"
    is Cat -> "Meow"
    else -> "..."
}

fun chorus(animals: List<Animal>): String = animals.joinToString(" ") { soundOf(it) }
