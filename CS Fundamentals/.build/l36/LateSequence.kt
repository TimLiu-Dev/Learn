import java.sql.DriverManager

fun main() {
    DriverManager.getConnection("jdbc:sqlite:library.db").use { c ->
        val titles: Sequence<String> = c.createStatement().use { st ->
            st.executeQuery("SELECT title FROM books ORDER BY id").use { rs ->
                generateSequence { if (rs.next()) rs.getString(1) else null }   // lazy: nothing read yet
            }
        }                                                   // ResultSet and Statement are closed here
        println(titles.take(2).toList())
    }
}
