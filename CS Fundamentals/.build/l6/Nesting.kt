class Playlist(val name: String) {
    private val songs = mutableListOf<String>()
    fun add(song: String) { songs += song }

    // Nested (Kotlin's default): no link to any Playlist
    class Song(val title: String, val seconds: Int)

    // inner: each Cursor belongs to one Playlist and can read its private songs
    inner class Cursor {
        private var index = 0
        fun next(): String? = songs.getOrNull(index++)
        fun owner() = this@Playlist.name   // the outer instance, by name
    }
}

fun main() {
    val intro = Playlist.Song("Intro", 90)       // no Playlist needed
    val mix = Playlist("Mix")
    mix.add(intro.title)
    mix.add("Outro")
    val cursor = mix.Cursor()                     // needs a Playlist to belong to
    println("${cursor.next()} ${cursor.next()} ${cursor.next()} (from ${cursor.owner()})")

    // A peek at the compiled fields: which class holds a hidden Playlist?
    println("Song:   " + Playlist.Song::class.java.declaredFields.map { it.name }.sorted())
    println("Cursor: " + Playlist.Cursor::class.java.declaredFields.map { it.name }.sorted())
}
