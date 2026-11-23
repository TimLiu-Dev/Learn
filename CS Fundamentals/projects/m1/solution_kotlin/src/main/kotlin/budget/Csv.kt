package budget

/** One CSV line split into fields, or the reason it couldn't be split. */
fun splitCsvLine(line: String): Parsed<List<String>> {
    val fields = mutableListOf<String>()
    var i = 0
    while (true) {
        if (i < line.length && line[i] == '"') {
            // Quoted field: commas are literal and "" is one quote character.
            val field = StringBuilder()
            i++
            while (true) {
                if (i >= line.length) return Parsed.Bad("unterminated quoted field")
                if (line[i] == '"') {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        field.append('"')
                        i += 2
                    } else {
                        i++
                        break
                    }
                } else {
                    field.append(line[i++])
                }
            }
            if (i < line.length && line[i] != ',') return Parsed.Bad("text after closing quote")
            fields += field.toString()
        } else {
            val end = line.indexOf(',', i).let { if (it < 0) line.length else it }
            fields += line.substring(i, end)
            i = end
        }
        if (i >= line.length) return Parsed.Ok(fields)
        i++ // skip the comma; a trailing comma leaves one empty field
    }
}
