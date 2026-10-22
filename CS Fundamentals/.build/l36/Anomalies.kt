import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

const val URL = "jdbc:h2:mem:bank;DB_CLOSE_DELAY=-1"   // one in-memory DB, shared by name

fun open(level: String): Connection = DriverManager.getConnection(URL).apply {
    createStatement().use {
        it.execute("SET LOCK_TIMEOUT 300")                                    // wait 0.3 s at most
        it.execute("SET SESSION CHARACTERISTICS AS TRANSACTION ISOLATION LEVEL $level")
    }
    autoCommit = false
}

fun Connection.int(sql: String): Int = createStatement().use { st -> st.executeQuery(sql).use { it.next(); it.getInt(1) } }
fun Connection.run(sql: String) = createStatement().use { it.executeUpdate(sql) }

fun reset() = DriverManager.getConnection(URL).use { c ->
    c.run("DROP TABLE IF EXISTS accounts"); c.run("DROP TABLE IF EXISTS doctors")
    c.run("CREATE TABLE accounts (id INT PRIMARY KEY, owner VARCHAR(20), balance_cents INT NOT NULL)")
    c.run("INSERT INTO accounts VALUES (1, 'Ada', 10000), (2, 'Bob', 5000)")
    c.run("CREATE TABLE doctors (name VARCHAR(20) PRIMARY KEY, on_call BOOLEAN)")
    c.run("INSERT INTO doctors VALUES ('alice', TRUE), ('bob', TRUE)")
}

// Each scenario interleaves two transactions step by step on ONE thread, so the output is reproducible.
fun dirtyRead(t1: Connection, t2: Connection): String {
    t1.run("UPDATE accounts SET balance_cents = 0 WHERE id = 1")             // not committed
    val seen = t2.int("SELECT balance_cents FROM accounts WHERE id = 1")
    t1.rollback()
    return if (seen == 0) "HAPPENS (saw 0)" else "no (saw $seen)"
}

fun nonRepeatableRead(t1: Connection, t2: Connection): String {
    val first = t1.int("SELECT balance_cents FROM accounts WHERE id = 1")
    t2.run("UPDATE accounts SET balance_cents = 9000 WHERE id = 1"); t2.commit()
    val second = t1.int("SELECT balance_cents FROM accounts WHERE id = 1")
    return if (first != second) "HAPPENS ($first, $second)" else "no ($first, $second)"
}

fun phantomRead(t1: Connection, t2: Connection): String {
    val first = t1.int("SELECT COUNT(*) FROM accounts WHERE balance_cents > 1000")
    t2.run("INSERT INTO accounts VALUES (3, 'Cy', 7000)"); t2.commit()
    val second = t1.int("SELECT COUNT(*) FROM accounts WHERE balance_cents > 1000")
    return if (first != second) "HAPPENS ($first, $second)" else "no ($first, $second)"
}

fun lostUpdate(t1: Connection, t2: Connection): String {
    val b1 = t1.int("SELECT balance_cents FROM accounts WHERE id = 1")       // both read 10000
    val b2 = t2.int("SELECT balance_cents FROM accounts WHERE id = 1")
    t1.run("UPDATE accounts SET balance_cents = ${b1 + 1000} WHERE id = 1"); t1.commit()
    return try {
        t2.run("UPDATE accounts SET balance_cents = ${b2 + 1000} WHERE id = 1"); t2.commit()
        "HAPPENS (final ${t1.int("SELECT balance_cents FROM accounts WHERE id = 1")})"
    } catch (e: SQLException) { "no: T2 got ${e.sqlState}" }
}

fun writeSkew(t1: Connection, t2: Connection): String {
    // Rule: at least one doctor must stay on call.
    val n1 = t1.int("SELECT COUNT(*) FROM doctors WHERE on_call")            // both check first: 2 on call
    val n2 = t2.int("SELECT COUNT(*) FROM doctors WHERE on_call")
    if (n1 >= 2) t1.run("UPDATE doctors SET on_call = FALSE WHERE name = 'alice'")
    if (n2 >= 2) t2.run("UPDATE doctors SET on_call = FALSE WHERE name = 'bob'")
    t1.commit(); t2.commit()
    val left = t1.int("SELECT COUNT(*) FROM doctors WHERE on_call")
    return if (left == 0) "HAPPENS (0 on call)" else "no ($left on call)"
}

fun main() {
    val levels = listOf("READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ", "SERIALIZABLE")
    val scenarios = listOf("dirty read" to ::dirtyRead, "non-repeatable read" to ::nonRepeatableRead,
                           "phantom read" to ::phantomRead, "lost update" to ::lostUpdate, "write skew" to ::writeSkew)
    for (level in levels) {
        println("== $level")
        for ((name, scenario) in scenarios) {
            reset()
            open(level).use { t1 -> open(level).use { t2 -> println("  %-20s %s".format(name, scenario(t1, t2))) } }
        }
    }
}
