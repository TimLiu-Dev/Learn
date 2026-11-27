data class Event(val type: String, val data: String, val id: String?)

// Parse a whole text/event-stream body into the events a browser's EventSource would fire.
fun parseSse(stream: String): List<Event> =
    TODO("go line by line: blank line -> dispatch, ':' -> comment, otherwise field and value")
