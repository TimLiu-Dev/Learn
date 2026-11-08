interface Priced {
    val name: String
    val price: Double
    fun label(): String = "$name: $price"          // a default method: no keyword needed
}

abstract class Product(override val name: String, private val basePrice: Double) : Priced {
    abstract fun taxRate(): Double
    override val price: Double
        get() = basePrice * (1 + taxRate())         // the state lives here, not in the interface
}

class Book(name: String, basePrice: Double) : Product(name, basePrice) {
    override fun taxRate(): Double = 0.0
}

class Gadget(name: String, basePrice: Double) : Product(name, basePrice) {
    override fun taxRate(): Double = 0.2
}

fun labels(items: List<Priced>): List<String> = items.map { it.label() }
