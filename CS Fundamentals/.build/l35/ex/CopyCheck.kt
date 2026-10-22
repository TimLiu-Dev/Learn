import java.sql.DriverManager
import java.sql.SQLException

// Prove the new schema rejects bad copies. Usage: ... CopyCheckKt copies.db
fun main(args: Array<String>) {
    val attempts = mapOf(
        "duplicate barcode" to "INSERT INTO copies VALUES ('C-0001', 4, 'SF-2', 'good')",
        "unknown condition" to "INSERT INTO copies VALUES ('C-0006', 4, 'SF-2', 'lost')",
        "unknown shelf"     to "INSERT INTO copies VALUES ('C-0007', 4, 'XX-9', 'good')",
        "valid copy"        to "INSERT INTO copies VALUES ('C-0008', 4, 'SF-2', 'new')",
    )
    DriverManager.getConnection("jdbc:sqlite:${args[0]}").use { c ->
        c.createStatement().use { it.execute("PRAGMA foreign_keys = ON") }
        for ((label, sql) in attempts) {
            val result = try {
                c.createStatement().use { "OK, ${it.executeUpdate(sql)} row" }
            } catch (e: SQLException) {
                e.message!!.substringAfter("(").removeSuffix(")")
            }
            println("%-18s %s".format(label, result))
        }
    }
}
