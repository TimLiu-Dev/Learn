fun main() {
    val requested = "666".toInt(8)       // what touch asks for
    val umask = "007".toInt(8)           // a team folder's umask
    val mode = requested and umask.inv()
    println(mode.toString(8))
}
