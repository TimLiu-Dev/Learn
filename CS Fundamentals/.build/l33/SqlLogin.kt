import java.sql.Connection
import java.sql.DriverManager

fun setUp(): Connection {
    val db = DriverManager.getConnection("jdbc:h2:mem:demo")  // in-memory, gone when the program exits
    db.createStatement().use { st ->
        st.execute("CREATE TABLE users(name VARCHAR(20), password VARCHAR(20))")
        st.execute("INSERT INTO users VALUES ('ana', 'p4ss'), ('ben', 'secret')")
    }
    return db
}

// VULNERABLE: user input is pasted into the SQL text
fun loginConcat(db: Connection, name: String, pw: String): Boolean {
    val sql = "SELECT name FROM users WHERE name = '$name' AND password = '$pw'"
    println("  SQL: $sql")
    db.createStatement().use { st -> st.executeQuery(sql).use { rs -> return rs.next() } }
}

// FIXED: the SQL text is fixed; input travels separately as parameters
fun loginPrepared(db: Connection, name: String, pw: String): Boolean =
    db.prepareStatement("SELECT name FROM users WHERE name = ? AND password = ?").use { ps ->
        ps.setString(1, name)
        ps.setString(2, pw)
        ps.executeQuery().use { it.next() }
    }

fun main() {
    setUp().use { db ->
        val evil = "' OR '1'='1"
        println("concat, right password:  ${loginConcat(db, "ana", "p4ss")}")
        println("concat, wrong password:  ${loginConcat(db, "ana", "nope")}")
        println("concat, crafted input:   ${loginConcat(db, "ana", evil)}")
        println("prepared, right password: ${loginPrepared(db, "ana", "p4ss")}")
        println("prepared, crafted input:  ${loginPrepared(db, "ana", evil)}")
    }
}
