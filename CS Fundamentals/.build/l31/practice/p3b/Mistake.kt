data class Event(val type: String, val data: String, val id: String?)

// Trims the value instead of removing one leading space.
fun parseSse(stream: String): List<Event> {
    val events = mutableListOf<Event>()
    var type = ""
    val data = StringBuilder()
    var lastId: String? = null
    for (line in stream.lines()) {
        when {
            line.isEmpty() -> {
                if (data.isNotEmpty()) events += Event(type.ifEmpty { "message" }, data.removeSuffix("\n").toString(), lastId)
                type = ""
                data.clear()
            }
            line.startsWith(":") -> {}
            else -> {
                val field = line.substringBefore(':')
                val value = line.substringAfter(':', "").trim()
                when (field) {
                    "data" -> data.append(value).append('\n')
                    "event" -> type = value
                    "id" -> lastId = value
                }
            }
        }
    }
    return events
}
