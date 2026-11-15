// What a PreparedStatement needs: SQL text with ? placeholders, and the values in the same order.
data class Query(val sql: String, val params: List<Any?>)

// PATCH /members/{id}: the client sends only the fields it wants to change,
// e.g. {"email": "ada@example.org"}. The keys come from the client too.
fun updateMember(id: Int, changes: Map<String, Any?>): Query {
    require(changes.isNotEmpty()) { "nothing to update" }
    // Every value is bound, but the keys are still glued in unchecked.
    val sets = changes.keys.joinToString(", ") { "$it = ?" }
    return Query("UPDATE members SET $sets WHERE id = ?", changes.values + id)
}
