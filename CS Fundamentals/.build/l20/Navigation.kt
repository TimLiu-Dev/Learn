import java.util.TreeMap

fun main() {
    // Departure times (minutes after midnight) -> train. Kept sorted by key.
    val trains = TreeMap(mapOf(
        375 to "06:15 local", 420 to "07:00 express", 465 to "07:45 local",
        510 to "08:30 express", 600 to "10:00 local"))
    println(trains.javaClass.name)

    val now = 450                                    // it's 07:30
    println("last train before now:  " + trains[trains.floorKey(now)])
    println("next train from now:    " + trains[trains.ceilingKey(now)])
    println("first / last:           ${trains.firstKey()} / ${trains.lastKey()}")
    println("before 08:00 (480):     " + trains.headMap(480).values)
    println("from 08:00 on:          " + trains.tailMap(480).values)
    println("after 10:00:            " + trains.ceilingKey(601))
}
