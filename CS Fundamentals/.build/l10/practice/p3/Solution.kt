sealed interface AgeResult {
    data class Ok(val age: Int) : AgeResult
    data class NotANumber(val text: String) : AgeResult
    data object Missing : AgeResult
}

// A row looks like "name,age", e.g. "Ada,36".
fun readAge(row: String): AgeResult {
    val fields = row.split(",")
    if (fields.size < 2 || fields[1].isBlank()) return AgeResult.Missing
    val text = fields[1].trim()
    // toIntOrNull: no exception at all for the failure we expect
    val age = text.toIntOrNull() ?: return AgeResult.NotANumber(text)
    return AgeResult.Ok(age)
}
