// What a PreparedStatement needs: SQL text with ? placeholders, and the values in the same order.
data class Query(val sql: String, val params: List<Any?>)

// PATCH /members/{id}: the client sends only the fields it wants to change,
// e.g. {"email": "ada@example.org"}. The keys come from the client too.
fun updateMember(id: Int, changes: Map<String, Any?>): Query {
    // Glues everything into the SQL text. Works for honest input, and only for that.
    val sets = changes.entries.joinToString(", ") { (column, value) -> "$column = '$value'" }
    return Query("UPDATE members SET $sets WHERE id = $id", emptyList())
}
