data class Event(val type: String, val data: String, val id: String?)

// Parse a whole text/event-stream body into the events a browser's EventSource would fire.
fun parseSse(stream: String): List<Event> {
    val events = mutableListOf<Event>()
    var type = ""
    val data = StringBuilder()
    var lastId: String? = null                                // survives from one event to the next
    for (line in stream.lines()) {                            // lines() accepts \n, \r\n and \r
        when {
            line.isEmpty() -> {                               // blank line: dispatch
                if (data.isNotEmpty()) events += Event(type.ifEmpty { "message" }, data.removeSuffix("\n").toString(), lastId)
                type = ""
                data.clear()
            }
            line.startsWith(":") -> {}                        // comment
            else -> {
                val field = line.substringBefore(':')
                val value = line.substringAfter(':', "").removePrefix(" ")   // only ONE space
                when (field) {
                    "data" -> data.append(value).append('\n')
                    "event" -> type = value
                    "id" -> lastId = value
                }                                             // retry and unknown fields: ignored here
            }
        }
    }
    return events                                             // an unfinished event is never dispatched
}
