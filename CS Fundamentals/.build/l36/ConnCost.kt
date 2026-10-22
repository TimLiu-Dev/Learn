import java.sql.Connection
import java.sql.DriverManager

const val N = 500

fun Connection.countBooks(): Int = createStatement().use { st ->
    st.executeQuery("SELECT COUNT(*) FROM books").use { it.next(); it.getInt(1) }
}

fun main() {
    val url = "jdbc:sqlite:library.db"
    repeat(2) { round ->                                  // round 1 includes JIT warm-up
        var t0 = System.nanoTime()
        repeat(N) { DriverManager.getConnection(url).use { it.countBooks() } }
        val fresh = (System.nanoTime() - t0) / 1e6

        t0 = System.nanoTime()
        DriverManager.getConnection(url).use { c -> repeat(N) { c.countBooks() } }
        val reused = (System.nanoTime() - t0) / 1e6
        println("round ${round + 1}: $N queries, new connection each %.1f ms, one reused connection %.1f ms".format(fresh, reused))
    }
}
