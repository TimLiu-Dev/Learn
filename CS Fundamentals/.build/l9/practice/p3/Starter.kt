import java.io.IOException

class Playlist(val name: String) {
    private val tracks = mutableListOf<String>()
    val size: Int get() = tracks.size

    fun add(title: String, times: Int = 1) {
        repeat(times) { tracks.add(title) }
    }

    fun load(text: String) {
        if (text.isBlank()) throw IOException("nothing to load")
        text.lines().forEach { add(it.trim()) }
    }
}

object Library {
    private val playlists = mutableListOf<Playlist>()
    fun register(p: Playlist) { playlists.add(p) }
    val count: Int get() = playlists.size
}
