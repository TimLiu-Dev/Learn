sealed interface Door {
    data object Open : Door
    data object Closed : Door
    data object Locked : Door
}
enum class Cmd { OPEN, CLOSE, LOCK, UNLOCK }

// A command the current state doesn't accept leaves the state unchanged.
fun next(door: Door, cmd: Cmd): Door = when (door) {
    Door.Open -> if (cmd == Cmd.CLOSE) Door.Closed else door
    Door.Closed -> when (cmd) {
        Cmd.OPEN -> Door.Open
        Cmd.LOCK -> Door.Locked
        else -> door
    }
    Door.Locked -> if (cmd == Cmd.UNLOCK) Door.Closed else door
}

fun main() {
    val cmds = listOf(Cmd.LOCK, Cmd.CLOSE, Cmd.LOCK, Cmd.OPEN, Cmd.UNLOCK)
    println(cmds.fold(Door.Open as Door, ::next))
}
