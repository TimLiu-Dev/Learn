// cut -d"$delim" -f"$field"
fun cut(lines: List<String>, delim: Char, field: Int): List<String> = lines.map { line ->
    if (delim !in line) line else line.split(delim).getOrElse(field - 1) { "" }
}

// uniq -c: only ADJACENT equal lines are merged
fun uniqC(lines: List<String>): List<Pair<Int, String>> {
    val runs = mutableListOf<Pair<Int, String>>()
    for (line in lines) {
        val last = runs.lastOrNull()
        if (last != null && last.second == line) runs[runs.lastIndex] = (last.first + 1) to line
        else runs += 1 to line
    }
    return runs
}

// sort | uniq -c | sort -rn | head -n $n
// sortedByDescending is stable (L18), so equal counts keep the alphabetical order of sorted().
fun topN(lines: List<String>, n: Int): List<Pair<Int, String>> =
    uniqC(lines.sorted()).sortedByDescending { it.first }.take(n)
