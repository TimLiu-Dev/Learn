class Vehicle(val wheels: Int) {
    fun describe(): String = "vehicle with $wheels wheels"
    fun honk(): String = "beep"
}

class Bike : Vehicle(2)

class Truck(val load: Int) : Vehicle(6) {
    fun describe(): String = TODO("reuse the superclass text, then add the load")
}
