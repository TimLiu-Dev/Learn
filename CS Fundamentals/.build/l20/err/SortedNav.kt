fun main() {
    val trains = sortedMapOf(375 to "06:15 local", 420 to "07:00 express")
    println(trains.javaClass.name)          // what object is it really?
    println(trains.floorKey(400))           // a TreeMap method...
}
