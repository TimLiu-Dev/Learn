interface Priced {
    val name: String
    val price: Double
    fun label(): String = TODO("\"NAME: PRICE\", for example \"Dune: 10.0\"")
}

abstract class Product(override val name: String, private val basePrice: Double) : Priced {
    abstract fun taxRate(): Double
    override val price: Double
        get() = TODO("base price × (1 + tax rate)")
}

class Book(name: String, basePrice: Double) : Product(name, basePrice) {
    override fun taxRate(): Double = TODO("books have no tax")
}

class Gadget(name: String, basePrice: Double) : Product(name, basePrice) {
    override fun taxRate(): Double = TODO("gadgets have 20% tax")
}

fun labels(items: List<Priced>): List<String> = TODO("one label per item")
