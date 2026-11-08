class Playlist(val maxSize: Int) {
    private val items = mutableListOf<String>()

    val songs: List<String>
        get() = items.toList()                    // a copy: callers can't reach items

    fun add(title: String) {
        check(items.size < maxSize) { "playlist is full" }
        items.add(title.trim())
    }
}
