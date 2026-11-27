class Playlist(val name: String) {
    private val songs = mutableListOf<String>()

    class Song(val title: String) {
        fun isFirst() = songs.firstOrNull() == title   // which Playlist's songs?
    }
}
