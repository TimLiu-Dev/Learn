// What a PreparedStatement needs: SQL text with ? placeholders, and the values in the same order.
data class Query(val sql: String, val params: List<Any?>)

// The only column names that may ever reach the SQL text.
val updatableColumns = setOf("name", "email")

// PATCH /members/{id}: the client sends only the fields it wants to change,
// e.g. {"email": "ada@example.org"}. The keys come from the client too.
fun updateMember(id: Int, changes: Map<String, Any?>): Query {
    require(changes.isNotEmpty()) { "nothing to update" }
    for (column in changes.keys) {
        require(column in updatableColumns) { "can't update '$column'" }
    }
    // Column names: our own strings, checked above. Values: always placeholders.
    val sets = changes.keys.joinToString(", ") { "$it = ?" }
    return Query("UPDATE members SET $sets WHERE id = ?", changes.values + id)
}
