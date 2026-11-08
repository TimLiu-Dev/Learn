open class Animal(val name: String)
class Cat(name: String) : Animal(name)

interface Kennel<T> {                  // hands animals out
    fun release(): T
    fun size(): Int
}

interface Vet<T> {                     // takes animals in
    fun treat(animal: T): String
}

fun rollCall(animals: Array<Animal>): String = animals.joinToString(" ") { it.name }

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
