open class Vehicle(val wheels: Int) {
    open fun describe(): String = "vehicle with $wheels wheels"
    fun honk(): String = "beep"                    // not open: subclasses can't change it
}

class Bike : Vehicle(2)

class Truck(val load: Int) : Vehicle(6) {
    override fun describe(): String = super.describe() + ", carrying $load kg"
}
