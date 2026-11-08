open class Animal {
    open fun sound(): String = "..."
}

class Dog : Animal() { override fun sound() = "Woof" }
class Cat : Animal() { override fun sound() = "Meow" }
class Cow : Animal() { override fun sound() = "Moo" }

fun soundOf(a: Animal): String = when (a) {
    is Dog -> "Woof"
    is Cat -> "Meow"
    is Cow -> "Moo"
    else -> "..."
}

fun chorus(animals: List<Animal>): String = animals.joinToString(" ") { soundOf(it) }
