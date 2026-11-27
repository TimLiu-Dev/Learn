package chat

val ROOM_NAME = Regex("[A-Za-z0-9_-]{1,32}")
const val MAX_TEXT = 4000

/** What one client frame parses to: a command, or the error to reply with. */
sealed interface Parsed

sealed interface Command : Parsed {
    data class Auth(val token: String) : Command
    data class Join(val room: String) : Command
    data class Leave(val room: String) : Command
    data class Msg(val room: String, val text: String) : Command
    data class Who(val room: String) : Command
    data object Quit : Command
}

data class Err(val code: Int, val reason: String) : Parsed {
    override fun toString() = "ERR $code $reason"
}

/** The whole protocol grammar as a pure function: no sockets, easy to unit-test. */
fun parseCommand(line: String): Parsed {
    val word = line.substringBefore(' ')
    val rest: String? = if (' ' in line) line.substringAfter(' ') else null
    fun oneArg(): String? = rest?.takeIf { it.isNotEmpty() && ' ' !in it }
    fun room(make: (String) -> Command): Parsed {
        val name = oneArg() ?: return Err(400, "bad_args")
        return if (ROOM_NAME.matches(name)) make(name) else Err(400, "bad_room")
    }
    return when (word) {
        "AUTH" -> oneArg()?.let { Command.Auth(it) } ?: Err(400, "bad_args")
        "JOIN" -> room(Command::Join)
        "LEAVE" -> room(Command::Leave)
        "WHO" -> room(Command::Who)
        "QUIT" -> if (rest == null) Command.Quit else Err(400, "bad_args")
        "MSG" -> {
            if (rest == null || ' ' !in rest) return Err(400, "bad_args")
            val room = rest.substringBefore(' ')
            val text = rest.substringAfter(' ')
            when {
                !ROOM_NAME.matches(room) -> Err(400, "bad_room")
                text.isEmpty() || text.any { it.isISOControl() } -> Err(400, "bad_text")
                text.encodeToByteArray().size > MAX_TEXT -> Err(400, "text_too_long")
                else -> Command.Msg(room, text)
            }
        }
        else -> Err(400, "unknown_command")
    }
}
