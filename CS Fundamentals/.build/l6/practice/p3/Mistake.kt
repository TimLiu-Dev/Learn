class Playlist(val maxSize: Int) {
    private val items = mutableListOf<String>()

    val songs: List<String>
        get() = items                             // read-only type, but the same mutable object

    fun add(title: String) {
        check(items.size < maxSize) { "playlist is full" }
        items.add(title.trim())
    }
}
