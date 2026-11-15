import java.io.IOException

class Playlist(@JvmField val name: String) {          // Java: p.name, a public field
    private val tracks = mutableListOf<String>()
    val size: Int get() = tracks.size

    @JvmOverloads                                      // Java also gets add(String)
    fun add(title: String, times: Int = 1) {
        repeat(times) { tracks.add(title) }
    }

    @Throws(IOException::class)                        // Java may (and must) catch it
    fun load(text: String) {
        if (text.isBlank()) throw IOException("nothing to load")
        text.lines().forEach { add(it.trim()) }
    }
}

object Library {
    private val playlists = mutableListOf<Playlist>()
    @JvmStatic fun register(p: Playlist) { playlists.add(p) }   // Java: Library.register(p)
    val count: Int get() = playlists.size
}
