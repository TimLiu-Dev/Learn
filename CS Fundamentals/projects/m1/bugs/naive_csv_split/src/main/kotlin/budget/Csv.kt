package budget

/** One CSV line split into fields, or the reason it couldn't be split. */
fun splitCsvLine(line: String): Parsed<List<String>> =
    Parsed.Ok(line.split(",")) // BUG: a comma inside quotes splits the field, and quotes are kept
