class Playlist(val maxSize: Int) {
    val items = mutableListOf<String>()           // TODO: nobody outside should touch this

    val songs: List<String>
        get() = items                             // TODO: is a read-only type enough?

    fun add(title: String) {
        check(items.size < maxSize) { "playlist is full" }
        items.add(title.trim())
    }
}
