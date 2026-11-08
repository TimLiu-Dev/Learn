open class Animal(val name: String)
class Cat(name: String) : Animal(name)

interface Kennel<out T> {              // T only comes out (a return type), so Kennel is covariant
    fun release(): T
    fun size(): Int
}

interface Vet<in T> {                  // T only goes in (a parameter), so Vet is contravariant
    fun treat(animal: T): String
}

// Array is invariant, so project this one use: we only read from it
fun rollCall(animals: Array<out Animal>): String = animals.joinToString(" ") { it.name }

// ───────────── leave everything below this line unchanged ─────────────

class CatKennel(vararg cats: Cat) : Kennel<Cat> {
    private val cats = cats.toMutableList()
    override fun release(): Cat = cats.removeAt(0)
    override fun size(): Int = cats.size
}

object AnyVet : Vet<Animal> {
    override fun treat(animal: Animal): String = "treated ${animal.name}"
}

fun releaseFirst(kennel: Kennel<Animal>): String = kennel.release().name
fun checkUp(cat: Cat, vet: Vet<Cat>): String = vet.treat(cat)

fun kennelDemo(): String = releaseFirst(CatKennel(Cat("Mia"), Cat("Tom")))   // Kennel<Cat> as Kennel<Animal>
fun vetDemo(): String = checkUp(Cat("Kit"), AnyVet)                           // Vet<Animal> as Vet<Cat>
fun rollCallDemo(): String {
    val cats: Array<Cat> = arrayOf(Cat("Mia"), Cat("Tom"))
    return rollCall(cats)                                                     // Array<Cat> as Array<Animal>
}
