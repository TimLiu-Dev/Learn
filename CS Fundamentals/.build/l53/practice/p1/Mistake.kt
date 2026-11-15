fun cut(lines: List<String>, delim: Char, field: Int): List<String> = lines.map { line ->
    if (delim !in line) line else line.split(delim).getOrElse(field - 1) { "" }
}

fun uniqC(lines: List<String>): List<Pair<Int, String>> {
    val runs = mutableListOf<Pair<Int, String>>()
    for (line in lines) {
        val last = runs.lastOrNull()
        if (last != null && last.second == line) runs[runs.lastIndex] = (last.first + 1) to line
        else runs += 1 to line
    }
    return runs
}

// uniq -c without sort first
fun topN(lines: List<String>, n: Int): List<Pair<Int, String>> =
    uniqC(lines).sortedByDescending { it.first }.take(n)
