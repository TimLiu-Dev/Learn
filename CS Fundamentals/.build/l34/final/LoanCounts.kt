import java.sql.DriverManager
import java.sql.ResultSet

data class MemberLoans(val name: String, val loans: Int)

// One extension turns "while (rs.next())" into an ordinary list
fun <T> ResultSet.toList(row: (ResultSet) -> T): List<T> {
    val out = mutableListOf<T>()
    while (next()) out += row(this)
    return out
}

const val SQL = """
    SELECT m.name, count(l.id) AS loans
    FROM members m LEFT JOIN loans l ON l.member_id = m.id
    GROUP BY m.id
    ORDER BY loans DESC, m.name
"""

fun main() {
    val rows = DriverManager.getConnection("jdbc:sqlite:library.db").use { conn ->
        conn.prepareStatement(SQL).use { st ->
            st.executeQuery().use { rs -> rs.toList { MemberLoans(it.getString("name"), it.getInt("loans")) } }
        }
    }
    rows.forEach { println("%-18s %d".format(it.name, it.loans)) }
    println("members: ${rows.size}, total loans: ${rows.sumOf { it.loans }}")
}
