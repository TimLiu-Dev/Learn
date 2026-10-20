// Every login is remembered forever: the map lives in an object, so it is a GC root.
class Session(val user: String, val state: ByteArray)

object Sessions {
    val all = HashMap<Int, Session>()

    fun login(id: Int) {
        all[id] = Session("user$id", ByteArray(100_000))   // ~100 KB each
    }
}

fun main() {
    var id = 0
    while (true) {
        Sessions.login(id)  // ...and nobody ever calls Sessions.all.remove(id)
        if (id % 100 == 0) println("sessions: ${Sessions.all.size}")
        Thread.sleep(10)    // one login every 10 ms, like a server under load
        id++
    }
}
