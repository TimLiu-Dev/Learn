sealed interface AgeResult {
    data class Ok(val age: Int) : AgeResult
    data class NotANumber(val text: String) : AgeResult
    data object Missing : AgeResult
}

// A row looks like "name,age", e.g. "Ada,36".
fun readAge(row: String): AgeResult {
    val fields = row.split(",")
    return try {
        AgeResult.Ok(fields[2].trim().toInt())
    } catch (e: Exception) {
        AgeResult.NotANumber(fields.last().trim())
    }
}
